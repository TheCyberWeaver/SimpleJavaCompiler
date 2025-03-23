package org.example.simplejava.ASTTree.statements;

import org.example.simplejava.ASTTree.expressions.Expression;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.tools.StringFormatter;

import java.util.ArrayList;

public class WhileStatement extends Statement {
    private final Expression condition;
    private final Block whileBlock;

    public WhileStatement(Expression condition, Block whileBlock) {
        this.condition = condition;
        this.whileBlock = whileBlock;
    }

    @Override
    public String toString() {
        String str = "<WhileStatement> [condition= " + condition + "]\n" +
                StringFormatter.addTabToEachLine(whileBlock.toString()) +
                "</WhileStatement>\n";
        return str;
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();

        // different While statements have different label IDs
        String LabelID= context.generateLabelID();
        String whileConditionLabel="WHILE_CONDITION_"+LabelID;
        String whileStartLabel="WHILE_START_"+LabelID;
        String whileEndLabel="WHILE_END_"+LabelID;

        // Condition part
        codeList.add(";"+whileConditionLabel);
        codeList.addAll(StringFormatter.addTabToEachLine(condition.generate(context, result)));
        codeList.add("\tjmp ["+whileStartLabel+"]");
        codeList.add("\tjmp ["+whileEndLabel+"]");

        // Loop part
        codeList.add(";"+whileStartLabel);
        codeList.addAll(StringFormatter.addTabToEachLine(whileBlock.generate(context, result)));
        codeList.add("\tjmp ["+whileConditionLabel+"]");

        // End label
        codeList.add(";"+whileEndLabel);

        return codeList;
    }
}