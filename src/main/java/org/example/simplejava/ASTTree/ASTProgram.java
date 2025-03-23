package org.example.simplejava.ASTTree;

import org.example.simplejava.ASTTree.statements.Block;
import org.example.simplejava.ASTTree.statements.Function;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.tools.StringFormatter;

import java.util.ArrayList;

public class ASTProgram extends ASTNode {
    private Block mainFunction;
    private ArrayList<Function> functions;

    public ASTProgram() {}
    public ASTProgram(Block mainFunction, ArrayList<Function> functions) {
        this.mainFunction = mainFunction;
        this.functions = functions;
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> code = new ArrayList<>();

        code.add(";MainFunction");
        code.addAll(StringFormatter.addTabToEachLine(mainFunction.generate(context, result)));
        code.add("\thlt");

        code.add(";Functions");
        if (functions != null) {
            for (Function f : functions) {
                code.addAll(StringFormatter.addTabToEachLine(f.generate(context, result)));
            }
        }

        return code;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(mainFunction.toString());
        for (Function f : functions) {
            str.append("\n");
            str.append(f.toString());
        }

        return str.toString();
    }
}