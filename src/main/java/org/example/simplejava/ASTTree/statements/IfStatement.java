package org.example.simplejava.ASTTree.statements;

import org.example.simplejava.ASTTree.expressions.Expression;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.tools.StringFormatter;

import java.util.ArrayList;

public class IfStatement extends Statement {
    private Expression condition;
    private Block thenBlock;
    private Block elseBlock;

    public IfStatement() {

    }

    public IfStatement(Expression condition, Block thenBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
    }

    public IfStatement(Expression condition, Block thenBlock, Block elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("<IfStatement - Condition>").append(condition).append("\n");
        if (thenBlock != null){
            str.append("<ThenBlock>\n").append(StringFormatter.addTabToEachLine(thenBlock.toString())).append("\n");
        }
        if (elseBlock != null){
            str.append("<ElseBlock>\n").append(StringFormatter.addTabToEachLine(elseBlock.toString())).append("\n");
        }
        str.append("</IfStatement>\n");

        return str.toString();
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();

        // different IF statements have different label IDs
        String LabelID=context.generateLabelID();

        String ifStartLabel="IF_START_"+LabelID;
        String ifThenLabel="IF_THEN_"+LabelID;
        String elseLabel="IF_ELSE_"+LabelID;
        String elseEndLabel="ELSE_END_"+LabelID;

        // Condition part
        codeList.add(";"+ifStartLabel);
        codeList.addAll(StringFormatter.addTabToEachLine(condition.generate(context, result)));

        codeList.add("\tjmp ["+ifThenLabel+"]");
        codeList.add("\tjmp ["+elseLabel+"]");

        // Then part
        codeList.add(";"+ifThenLabel);
        codeList.addAll(StringFormatter.addTabToEachLine(thenBlock.generate(context, result)));
        codeList.add("\tjmp ["+elseEndLabel+"]");


        // Else part (Optional)
        if(elseBlock!=null){
            codeList.add(";"+elseLabel);
            codeList.addAll(StringFormatter.addTabToEachLine(elseBlock.generate(context, result)));
        }
        // End label not optional
        codeList.add(";"+elseEndLabel);

        return codeList;
    }
}