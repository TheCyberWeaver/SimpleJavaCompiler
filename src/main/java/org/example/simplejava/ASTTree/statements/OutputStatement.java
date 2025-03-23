package org.example.simplejava.ASTTree.statements;

import org.example.simplejava.ASTTree.expressions.Expression;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;

import java.util.ArrayList;

public class OutputStatement extends Statement {
    private final Expression expression;

    public OutputStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "<OutputStatement> [expression=" + expression + "]";
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {

        result.setNeedExtendedInstructionSet(true); // Since Instruction "savi" is needed

        ArrayList<String> codeList = new ArrayList<>(expression.generate(context, result));

        // Store the output at the location pointed to by the output register.
        codeList.add("savi [OUT]");
        // decrease output register by one.
        codeList.add("dec [OUT]");
        return codeList;
    }
}