package org.example.simplejava.ASTTree.statements;

import org.example.simplejava.ASTTree.expressions.Expression;
import org.example.simplejava.ASTTree.expressions.Variable;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;

import java.util.ArrayList;

public class Assignment extends Statement {
    private final Variable targetVar;
    private final Expression value;

    public Assignment(Variable targetVar, Expression value) {
        this.targetVar = targetVar;
        this.value = value;
    }
    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();

        codeList.addAll(value.generate(context, result));

        codeList.add("save ["+ targetVar.getName().value()+"]");

        return codeList;
    }

    @Override
    public String toString() {
        return "<Assignment>: " + targetVar + " <With> " + value +"\n";
    }
}