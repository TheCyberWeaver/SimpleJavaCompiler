package org.example.simplejava.ASTTree.statements;

import org.example.simplejava.ASTTree.expressions.Expression;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;

import java.util.ArrayList;

public class ReturnStatement extends Statement {
    private final Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "<Return> "+expression.toString();
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();

        codeList.addAll(expression.generate(context, result));

        codeList.add("save [RET_VALUE]");

        return codeList;
    }
}