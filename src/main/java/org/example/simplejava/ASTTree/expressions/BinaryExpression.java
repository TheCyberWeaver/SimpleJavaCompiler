package org.example.simplejava.ASTTree.expressions;

import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.Token;

import java.util.ArrayList;

import static org.example.simplejava.helperObjects.MessageType.ERROR;

public class BinaryExpression extends Expression {
    private final Expression left;
    private final Token operator;
    private final Expression right;

    private int tempVariableCounter;

    public BinaryExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;

        this.right = right;
    }

    @Override
    public String toString() {
        return left + " " + operator + " " + right;
    }


    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();


        tempVariableCounter =0; // initialize the number of temporary variable used

        // Save the result of the left expression
        String tempLeft=generateExpressionOnOneSide(context, result, codeList, left);


        // Save the result of the right expression
        String tempRight=generateExpressionOnOneSide(context, result, codeList, right);

        int tmpAddr;

        // Generate different instructions depends on the operator
        switch (operator.value()){
            case "+":
                codeList.add("take " + tempLeft);
                codeList.add("add " + tempRight);
                break;
            case "-":
                codeList.add("take " + tempLeft);
                codeList.add("sub " + tempRight);
                break;
            case "<":
                codeList.add("take " + tempRight);
                codeList.add("sub " + tempLeft);

                tmpAddr = context.getNextTempAddress();
                tempVariableCounter++;

                codeList.add("save " + tmpAddr);
                codeList.add("tst " + tmpAddr);
                break;
            case "<=":
                codeList.add("take " + tempRight);
                codeList.add("add [1]"); //1 is predefined
                codeList.add("sub " + tempLeft);

                tmpAddr = context.getNextTempAddress();
                tempVariableCounter++;

                codeList.add("save " + tmpAddr);
                codeList.add("tst " + tmpAddr);
                break;
            case ">":

                codeList.add("take " + tempLeft);
                codeList.add("sub " + tempRight);

                tmpAddr=context.getNextTempAddress();
                tempVariableCounter++;

                codeList.add("save " + tmpAddr);
                codeList.add("tst " + tmpAddr);
                break;
            case ">=":
                codeList.add("take " + tempLeft);
                codeList.add("add [1]"); //1 is predefined
                codeList.add("sub " + tempRight);

                tmpAddr=context.getNextTempAddress();
                tempVariableCounter++;

                codeList.add("save " + tmpAddr);
                codeList.add("tst " + tmpAddr);
                break;
            default:
                result.addMessage(ERROR,"Unsupported operator in BinaryExpression: " + operator.value());
                break;
        }

        // Release the memory space used by the temporary variables
        for(int i = 0; i< tempVariableCounter; i++) {
            context.releaseTempAddress();
        }

        return codeList;
    }

    private String generateExpressionOnOneSide(CodeGenContext context, CompilationResult result, ArrayList<String> code, Expression expressionOnOneSide) {
        ArrayList<String> codeOnOneSide = expressionOnOneSide.generate(context, result);
        String referenceStr;

        if (expressionOnOneSide instanceof Integer_Literal) {
            referenceStr = "["+((Integer_Literal) expressionOnOneSide).getValue()+"]";
        }
        else if(expressionOnOneSide instanceof Variable){
            referenceStr = "["+((Variable) expressionOnOneSide).getName().value()+"]";
        }
        else{
            code.addAll(codeOnOneSide);
            referenceStr = context.getNextTempAddress()+"";
            code.add("save " + referenceStr);
            tempVariableCounter++;
        }
        return referenceStr;
    }
}