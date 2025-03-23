package org.example.simplejava.ASTTree.statements;

import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;

import java.util.ArrayList;

import static org.example.simplejava.tools.StringFormatter.addTabToEachLine;

public class Block extends Statement {

    public ArrayList<Statement> statements;

    public Block() {
        this.statements=new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public Block(ArrayList<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("<Block>{\n");
        if (statements != null) {
            for (Statement statement : statements) {
                if (statement != null) {
                    str.append(addTabToEachLine(statement.toString()));
                }
                else{
                    str.append(addTabToEachLine("<Null Statement>"));
                }
            }
        }
        str.append("}\n");

        return str.toString();
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();


        for (Statement stmt : statements) {
            ArrayList<String> codes = stmt.generate(context, result);
            if (codes != null) {
                codeList.addAll(codes);
            }
        }


        return codeList;
    }
}