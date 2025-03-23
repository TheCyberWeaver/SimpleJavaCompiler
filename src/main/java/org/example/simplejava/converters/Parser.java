package org.example.simplejava.converters;

import javafx.scene.paint.Color;
import org.example.simplejava.ASTTree.ASTProgram;
import org.example.simplejava.ASTTree.Parameter;
import org.example.simplejava.ASTTree.expressions.*;
import org.example.simplejava.ASTTree.statements.*;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.Token;
import org.example.simplejava.tools.MsgLibrary;

import java.util.ArrayList;

import static org.example.simplejava.helperObjects.MessageType.*;
import static org.example.simplejava.helperObjects.Token.Type.*;
import static org.example.simplejava.tools.MsgLibrary.*;

/**
 * {@code @Author:} Thomas Lu
 * The Parser
 */

public class Parser extends Converter<ArrayList<Token>, ASTProgram> {
    private final ArrayList<Variable> globalVariables = new ArrayList<>();
    private final ArrayList<Function> functions = new ArrayList<>();
    private final ArrayList<FunctionCall> functionCalls = new ArrayList<>();
    private ArrayList<Token> tokens = new ArrayList<>();
    private int pos = 0;

    public Parser(CompilationResult result) {
        super(result);
    }

    @Override
    public ASTProgram convert(ArrayList<Token> tokens) {
        result.addMessage("<======================== Parser Start ========================>", Color.VIOLET);
        this.tokens = tokens;
        // Parse main function
        Block mainFunction = parseMainFunction();
        result.addMessage("Parsing Functions", Color.VIOLET);
        // Parse all other functions
        while (pos < tokens.size()) {
            Function func = parseFunctionDeclaration();
            if (func != null) {
                functions.add(func);
            }
        }
        result.addMessage("Parsing Functions End", Color.VIOLET);
        for (Function f : functions) {
            result.addMessage(INFO, MsgLibrary.get(PARSING_FUNCTION,f.name));
        }
        //Debug
        for (FunctionCall functionCall : functionCalls) {
            result.addMessage(DEBUG, MsgLibrary.get(PARSING_FUNCTION_CALL,functionCall));
        }

        // Recheck if all function calls are defined
        for (FunctionCall functionCall : functionCalls) {
            boolean hasFoundFunctionDeclaration = false;
            for (Function function : functions) {
                if (functionCall.getFunctionName().value().equals(function.name.value())) {
                    hasFoundFunctionDeclaration = true;
                    break;
                }
            }
            if (!hasFoundFunctionDeclaration) {
                result.addMessage(WARNING, MsgLibrary.get(FUNCTION_NOT_DEFINED,functionCall.getFunctionName()));
            }
        }


        result.addMessage("<======================== Parser End ========================>", Color.VIOLET);
        return new ASTProgram(mainFunction, functions);
    }

    /**
     * Parse Statements
     */

    private Statement parseStatement() {
        switch (currentToken().type()) {
            // int
            case DATATYPE:
                return parseVarDeclaration();
            // Variable or Function Call
            case VARIABLE:
                return parseStatementStartingWithVariable(true);
            // STATEMENT_KEYWORD
            case STATEMENT_KEYWORD:
                return parseKeyWordStatement();
            // Separate Block
            case LEFT_BRACKET:
                return parseBlock();
            default:
                result.addMessage(ERROR, MsgLibrary.get(STATEMENT_NOT_KNOWN,currentToken().value()));
                advanceToNextStatement();
                break;
        }
        return null;
    }

    private Statement parseStatementStartingWithVariable(boolean doParseSemicolon) {
        Variable var = parseDeclaredVariable(currentToken());
        Token varToken = currentToken();
        advance();

        if (var == null) {
            if (currentToken().type() == LEFT_PAREN) {
                return parseFunctionCallWithOutReturn(new Variable(varToken));
            } else {
                result.addMessage(ERROR, "Variable neither as Variable nor as function declared: " + currentToken().value());
                return null;
            }
        }

        switch (currentToken().type()) {
            case ASSIGN:
                advance(ASSIGN);
                return parseAssignment(var);
            case MONO_OPERATOR:
                result.addMessage(INFO, MsgLibrary.get(ASSIGNING_VAR_MONO_OPERATOR,var));
                switch (currentToken().value()) {
                    case "+=":
                        advance(MONO_OPERATOR);

                        Expression expression = parseExpression();
                        if (doParseSemicolon) advance(SEMICOLON);
                        return new Assignment(var, new BinaryExpression(var, new Token(OPERATOR, "+"), expression));
                    case "-=":
                        advance(MONO_OPERATOR);

                        expression = parseExpression();
                        if (doParseSemicolon) advance(SEMICOLON);
                        return new Assignment(var, new BinaryExpression(var, new Token(OPERATOR, "-"), expression));
                    case "++":
                        advance(MONO_OPERATOR);
                        if (doParseSemicolon) advance(SEMICOLON);
                        return new Assignment(
                                var,
                                new BinaryExpression(
                                        var,
                                        new Token(OPERATOR, "+"),
                                        new Integer_Literal(new Token(INT_LITERAL, "1"))
                                )
                        );
                    case "--":
                        advance(MONO_OPERATOR);
                        if (doParseSemicolon) advance(SEMICOLON);
                        return new Assignment(
                                var,
                                new BinaryExpression(
                                        var,
                                        new Token(OPERATOR, "-"),
                                        new Integer_Literal(new Token(INT_LITERAL, "1"))
                                )
                        );
                    default:
                        result.addMessage(ERROR, "Operator not known" + currentToken().value());
                        advanceToNextStatement();
                        break;
                }
                break;
            default:
                result.addMessage(ERROR, "Token after Variable (Statement) not known");
                break;
        }
        return null;
    }

    private Statement parseKeyWordStatement() {
        switch (currentToken().value()) {
            case "if":
                advance(STATEMENT_KEYWORD);
                advance(LEFT_PAREN);
                Expression condition = parseExpression();
                advance(RIGHT_PAREN);
                Block ifThenBlock = parseBlock();
                if (currentToken().value().equals("else")) {
                    advance(STATEMENT_KEYWORD);
                    Block elseBlock = parseBlock();
                    return new IfStatement(condition, ifThenBlock, elseBlock);
                }
                return new IfStatement(condition, ifThenBlock);
            case "while":
                advance(STATEMENT_KEYWORD);
                advance(LEFT_PAREN);
                Expression whileCondition = parseExpression();
                advance(RIGHT_PAREN);
                Block whileBlock = parseBlock();
                return new WhileStatement(whileCondition, whileBlock);
            case "for":
                advance(STATEMENT_KEYWORD);
                advance(LEFT_PAREN);
                VarDeclaration forExp1 = parseVarDeclaration();
                Expression forCondition = parseExpression();
                advance(SEMICOLON);
                Statement incrementStatement = parseStatementStartingWithVariable(false);
                advance(RIGHT_PAREN);
                Block forBlock = parseBlock();
                return new ForStatement(forExp1, forCondition, incrementStatement, forBlock);
            case "return":
                advance(STATEMENT_KEYWORD);
                Expression returnValue = parseExpression();
                advance(SEMICOLON);
                return new ReturnStatement(returnValue);
            case "println":
                advance(STATEMENT_KEYWORD);
                advance(LEFT_PAREN);
                Expression printlnExpression = parseExpression();
                advance(RIGHT_PAREN);
                advance(SEMICOLON);
                return new OutputStatement(printlnExpression);
            default:
                result.addMessage(ERROR, "Unknown keyword (statement will be skipped): " + currentToken().value());
                advanceToNextStatement();
                break;
        }
        return null;
    }

    private VarDeclaration parseVarDeclaration() {
        Token datatype = currentToken();
        advance(DATATYPE);
        Token name = currentToken();
        advance(VARIABLE);
        Variable var = new Variable(name);


        if (currentToken().type() == SEMICOLON) {
            advance(SEMICOLON);
            result.addMessage(INFO, MsgLibrary.get(VAR_ADDED,var));
            globalVariables.add(var);

            return new VarDeclaration(datatype, var);
        } else if (currentToken().type() == ASSIGN) {
            advance(ASSIGN);
            globalVariables.add(var);

            Expression expression = parseExpression();
            advance(SEMICOLON);

            result.addMessage(INFO, MsgLibrary.get(VAR_ADDED_WITH_INIT,var,expression));
            return new VarDeclaration(datatype, var, expression);
        }
        result.addMessage(ERROR, "Token after Variable (Declaration) not known");
        return null;
    }

    private Statement parseAssignment(Variable var) {
        result.addMessage(INFO, MsgLibrary.get(ASSIGNING_VAR,var));
        Expression expression = parseExpression();
        advance(SEMICOLON);
        return new Assignment(var, expression);
    }

    private Block parseBlock() {
        result.addMessage(DEBUG, "Parsing Block...");

        Block block = new Block();
        advance(LEFT_BRACKET);

        while (currentToken().type() != RIGHT_BRACKET) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                block.statements.add(stmt);
            }
        }
        advance(RIGHT_BRACKET);

        result.addMessage(DEBUG, "Parsing Block End");
        return block;
    }

    /**
     * parse Expressions recursively
     * semicolon not included
     */
    private Expression parseExpression() {
        return parseRelational();
    }

    //Priority Level 0
    private Expression parseRelational() {
        Expression left = parseAdditive();

        while (currentToken().type() == OPERATOR &&
                (currentToken().value().equals(">") ||
                        currentToken().value().equals("<") ||
                        currentToken().value().equals(">=") ||
                        currentToken().value().equals("<="))) {
            Token operator = currentToken();
            advance(OPERATOR);
            Expression right = parseAdditive();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    //Priority Level 1
    private Expression parseAdditive() {
        Expression left = parseFactor();
        while (currentToken().type() == OPERATOR &&
                (currentToken().value().equals("+") ||
                        currentToken().value().equals("-"))) {
            Token operator = currentToken();
            advance(OPERATOR);
            Expression right = parseFactor();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    //Priority Level 2

    private Expression parseFactor() {
        Token token = currentToken();
        if (token.type() == LEFT_PAREN) {
            advance(LEFT_PAREN);
            Expression expr = parseExpression();
            advance(RIGHT_PAREN);
            return expr;
        } else if (token.type() == INT_LITERAL) {
            advance(INT_LITERAL);
            return new Integer_Literal(token);
        } else if (token.type() == VARIABLE) {
            Variable var = parseDeclaredVariable(token);
            advance(VARIABLE);
            if (var == null) {
                return parseFunctionCall(new Variable(token));
            }

            return var;
        }

        result.addMessage(ERROR, "Unexpected token in factor:" + token);
        advance();
        return null;
    }


    private Variable parseDeclaredVariable(Token readToken) {
        if (readToken.type() == VARIABLE) {
            for (Variable var : globalVariables) {
                if (var.getName().value().equals(readToken.value())) {
                    return var;
                }
            }
            result.addMessage(INFO, "Variable as Variable Not Defined: " + readToken);
        }
        return null;
    }

    /**
     * Functions Related
     */
    private Block parseMainFunction() {
        result.addMessage(DEBUG, "Parsing Main Function...");

        Block mainFunction = new Block();
        while (currentToken() != null) {
            if (currentToken().type() == MAIN_FUNCTION) {
                advance(MAIN_FUNCTION);
                mainFunction = parseBlock();
            } else {
                advance();
            }
        }
        pos = 0; //reset pointer

        result.addMessage(DEBUG, "Parsing Main Function End");
        return mainFunction;
    }

    private Function parseFunctionDeclaration() {
        Function function = new Function();

        while (currentToken() != null) {
            if (currentToken().type() == DATATYPE) {

                function.returnDataType = currentToken();
                advance(DATATYPE);

                function.name = currentToken();
                advance(VARIABLE);

                result.addMessage(INFO, "Parsing Function Declaration: " + function.name);

                function.parameters = parseParametersByDeclaration();
                function.functionBody = parseBlock();
                return function;

            } else if (currentToken().type() == LEFT_BRACKET) {
                int bracketCount = 1;
                //Skip block
                while (currentToken() != null && bracketCount != 0) {
                    if (currentToken().type() == RIGHT_BRACKET) {
                        bracketCount--;
                    }
                    if (currentToken().type() == LEFT_BRACKET) {
                        bracketCount++;
                    }
                    advance();
                }
            } else {
                advance();
            }
        }

        return null;
    }

    private FunctionCall parseFunctionCall(Variable var) {
        advance(LEFT_PAREN);
        ArrayList<Expression> parameters = new ArrayList<>();
        while (currentToken().type() != RIGHT_PAREN) {
            parameters.add(parseExpression());
            if (currentToken().type() == COMMA) {
                advance(COMMA);
            }
        }
        advance(RIGHT_PAREN);

        FunctionCall funcCall = new FunctionCall(var.getName(), parameters);

        // record all function calls
        functionCalls.add(funcCall);
        return funcCall;
    }

    private Statement parseFunctionCallWithOutReturn(Variable var) {
        FunctionCall funcCall = parseFunctionCall(var);
        advance(SEMICOLON);
        return new FunctionCallStatement(funcCall);
    }

    private ArrayList<Parameter> parseParametersByDeclaration() {
        ArrayList<Parameter> parameters = new ArrayList<>();
        advance(LEFT_PAREN);
        while (currentToken() != null && currentToken().type() != RIGHT_PAREN) {
            Token datatype = currentToken();
            advance(DATATYPE);

            Token name = currentToken();
            advance(VARIABLE);

            parameters.add(new Parameter(datatype, name));

            globalVariables.add(new Variable(name));

            if (currentToken() != null && currentToken().type() == COMMA) {
                advance(COMMA);
            }
        }
        advance(RIGHT_PAREN);
        return parameters;
    }

    /**
     * Helper Methods
     */

    private void advance(Token.Type type) {

        if (currentToken() == null) {
            return;
        }
        if (currentToken().type() == type) {
            //result.addMessage(SUCCESS, "Required Type:["+type+"] Detected Token: " +currentToken());
            advance();
        } else {
            result.addMessage(ERROR, MsgLibrary.get(TOKEN_UNEXPECTED_DURING_ADVANCE,type,currentToken()));
        }
    }

    private void advance() {
        if (pos < tokens.size()) {
            pos++;
        }
    }

    private void advanceToNextStatement() {
        while (currentToken() != null && currentToken().type() != SEMICOLON) {
            advance();
        }
        if (currentToken() == null) {
            return;
        }
        advance(SEMICOLON);
    }

    private Token currentToken() {
        if (pos < tokens.size()) {
            return tokens.get(pos);
        }
        return null;
    }
}
