package org.example.simplejava.ASTTree.expressions;

import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.Token;

import java.util.ArrayList;


public class Integer_Literal extends Expression {
    private final int value;

    public Integer_Literal(Token token) {
        value =Integer.parseInt(token.value());
    }

    @Override
    public String toString() {
        return value+"";
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();

        // constants used are stored in a list
        if(!context.getConstantsList().contains(value)){
            context.getConstantsList().add(value);
        }

        codeList.add("take [" + value+"]");

        return codeList;
    }
    public int getValue() {
        return value;
    }
}