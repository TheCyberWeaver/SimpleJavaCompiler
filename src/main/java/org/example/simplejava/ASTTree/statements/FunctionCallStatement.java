package org.example.simplejava.ASTTree.statements;

import org.example.simplejava.ASTTree.expressions.FunctionCall;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;

import java.util.ArrayList;

public class FunctionCallStatement extends Statement {
    private final FunctionCall functionCall;
    public FunctionCallStatement(FunctionCall functionCall) {
        this.functionCall = functionCall;
    }

    @Override
    public String toString() {
        return "<(void)> "+functionCall;
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        return functionCall.generate(context, result);
    }
}
