package org.example.simplejava.ASTTree.statements;

import org.example.simplejava.ASTTree.expressions.Expression;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.tools.StringFormatter;

import java.util.ArrayList;

public class ForStatement extends Statement {

    private final VarDeclaration variableDeclaration;
    private final Expression condition;
    private final Statement incrementStatement;
    private final Block forBlock;

    public ForStatement(VarDeclaration variableDeclaration, Expression condition, Statement incrementStatement, Block block) {
        this.variableDeclaration = variableDeclaration;
        this.condition = condition;
        this.incrementStatement = incrementStatement;
        this.forBlock = block;
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();

        // different For statements have different label IDs
        String LabelID = context.generateLabelID();
        String ForDefinitionLable = "FOR_DEFINITION_" + LabelID;
        String ForConditionLabel = "FOR_CONDITION_" + LabelID;
        String ForStartLabel = "FOR_START_" + LabelID;
        String ForEndLabel = "FOR_END_" + LabelID;

        // Definition part
        codeList.add(";" + ForDefinitionLable);
        codeList.addAll(variableDeclaration.generate(context, result));

        // Condition part
        codeList.add(";" + ForConditionLabel);
        codeList.addAll(StringFormatter.addTabToEachLine(condition.generate(context, result)));
        codeList.add("\tjmp [" + ForStartLabel + "]");
        codeList.add("\tjmp [" + ForEndLabel + "]");

        // Loop part
        codeList.add(";" + ForStartLabel);
        codeList.addAll(StringFormatter.addTabToEachLine(forBlock.generate(context, result)));
        // Increment part
        codeList.addAll(StringFormatter.addTabToEachLine(incrementStatement.generate(context, result)));
        // Jump back to Condition
        codeList.add("\tjmp [" + ForConditionLabel + "]");



        // End label
        codeList.add(";" + ForEndLabel);

        return codeList;
    }

    @Override
    public String toString() {
        String str = "<ForStatement> [condition= " + condition + "] [increment= " +
                incrementStatement.toString().substring(0, incrementStatement.toString().length() - 1) +
                "]\n" +
                StringFormatter.addTabToEachLine(forBlock.toString()) +
                "</ForStatement>\n";
        return str;
    }
}
