package org.example.simplejava.converters;

import javafx.scene.paint.Color;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.Token;
import org.example.simplejava.tools.MsgLibrary;

import java.util.ArrayList;

import static org.example.simplejava.helperObjects.MessageType.*;
import static org.example.simplejava.helperObjects.Token.Type.*;
import static org.example.simplejava.tools.MsgLibrary.*;

/**
 * {@code @Author:} Thomas Lu
 * The Lexer
 */

public class Lexer extends Converter<String, ArrayList<Token>> {
    private String fileString = "";

    private int pos = 0;
    private char currentChar;

    public Lexer(CompilationResult result) {
        super(result);
    }

    @Override
    public ArrayList<Token> convert(String input) {
        fileString = input;
        if (!fileString.isEmpty()) {
            this.currentChar = fileString.charAt(pos);
        }

        result.addMessage("<======================== Lexer Start ========================>", Color.AQUA);

        ArrayList<Token> tokenList = new ArrayList<>();

        // Iterate through the file
        Token token = getNextToken();
        while (token != null) {
            tokenList.add(token);
            token = getNextToken();
        }

        result.addMessage(INFO, "Total number of tokens: " + tokenList.size());
        result.addMessage("<======================== Lexer End ========================>", Color.AQUA);

        return tokenList;
    }

    private Token getNextToken() {
        while (currentChar != '\0') {

            // Skip spaces
            if (Character.isWhitespace(currentChar)) {
                while (Character.isWhitespace(currentChar) && currentChar != '\0') {
                    advance();
                }
                continue;
            }

            // Detect numbers
            if (Character.isDigit(currentChar)) {
                StringBuilder tmpStr = new StringBuilder();
                while (Character.isDigit(currentChar)) {
                    tmpStr.append(currentChar);
                    advance();
                }
                return new Token(INT_LITERAL, tmpStr.toString());
            }

            // Detect words (keywords or variable name)
            if (Character.isLetter(currentChar)) {
                StringBuilder tmpStr = new StringBuilder();
                while (Character.isLetter(currentChar) || Character.isDigit(currentChar) || currentChar == '_' || currentChar == '.') {
                    tmpStr.append(currentChar);
                    advance();
                }
                switch (tmpStr.toString()) {
                    case "int":
                        return new Token(DATATYPE, "int");
                    case "void":
                        return new Token(DATATYPE, "void");
                    case "if":
                        return new Token(STATEMENT_KEYWORD, "if");
                    case "else":
                        return new Token(STATEMENT_KEYWORD, "else");
                    case "while":
                        return new Token(STATEMENT_KEYWORD, "while");
                    case "for":
                        return new Token(STATEMENT_KEYWORD, "for");
                    case "return":
                        return new Token(STATEMENT_KEYWORD, "return");
                    case "System.out.println":
                        result.addMessage(INFO, MsgLibrary.get(SYSTEM_OUTPUT_DETECTED));
                        return new Token(STATEMENT_KEYWORD, "println");
                    case "public":
                        continue;
                    case "private":
                        result.addMessage(WARNING, MsgLibrary.get(PRIVATE_MODIFIER_DETECTED));
                        continue;
                    case "main":
                        result.addMessage(INFO, MsgLibrary.get(MAIN_FUNCTION_DETECTED));
                        return new Token(MAIN_FUNCTION, "main");
                    default:
                        result.addMessage(DEBUG, "Default to VARIABLE: " + tmpStr);
                        return new Token(VARIABLE, tmpStr.toString());
                }
            }

            // Ignore comments
            if (currentChar == '/') {
                advance();
                if (currentChar == '/') {
                    while (currentChar != '\n' && currentChar != '\r' && currentChar != '\0') {
                        advance();
                    }
                    continue;
                } else {
                    result.addMessage(ERROR, MsgLibrary.get(UNEXPECTED_CHARACTER,currentChar));
                }
            }

            // Detection of single characters that CANNOT be combined with other characters
            switch (currentChar) {
                case ';':
                    advance();
                    return new Token(SEMICOLON, ";");
                case ',':
                    advance();
                    return new Token(COMMA, ",");
                case '(':
                    advance();
                    return new Token(LEFT_PAREN, "(");
                case ')':
                    advance();
                    return new Token(RIGHT_PAREN, ")");
                case '{':
                    advance();
                    return new Token(LEFT_BRACKET, "{");
                case '}':
                    advance();
                    return new Token(RIGHT_BRACKET, "}");
                default:
                    break;
            }

            StringBuilder tmpStr = new StringBuilder();
            while (!Character.isLetter(currentChar) && !Character.isDigit(currentChar) && !Character.isWhitespace(currentChar)
                    && currentChar != '\0' && currentChar != '/' && currentChar != ';'
                    && currentChar != '(' && currentChar != ')' && currentChar != '{' && currentChar != '}') {
                tmpStr.append(currentChar);
                advance();
            }


            // Detection of characters that CAN be combined with other characters
            switch (tmpStr.toString()) {
                case "=":
                    return new Token(ASSIGN, "=");
                case "+":
                    return new Token(OPERATOR, "+");
                case "-":
                    return new Token(OPERATOR, "-");
                case "++":
                    return new Token(MONO_OPERATOR, "++");
                case "--":
                    return new Token(MONO_OPERATOR, "--");
                case "+=":
                    return new Token(MONO_OPERATOR, "+=");
                case "-=":
                    return new Token(MONO_OPERATOR, "-=");
                case ">":
                    return new Token(OPERATOR, ">");
                case "<":
                    return new Token(OPERATOR, "<");
                case ">=":
                    return new Token(OPERATOR, ">=");
                case "<=":
                    return new Token(OPERATOR, "<=");
                case "[]":
                    continue;
                default:
                    result.addMessage(ERROR, MsgLibrary.get(UNEXPECTED_CHARACTER,currentChar));
            }
        }

        result.addMessage(DEBUG, "returning null");
        return null;
    }

    // Move the currentChar pointer to the next position
    private void advance() {
        pos++;
        if (pos < fileString.length()) {
            currentChar = fileString.charAt(pos);
        } else {
            currentChar = '\0';
        }
    }
}
