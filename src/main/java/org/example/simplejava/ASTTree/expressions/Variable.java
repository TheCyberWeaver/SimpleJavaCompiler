package org.example.simplejava.ASTTree.expressions;

import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.Token;

import java.util.ArrayList;

public class Variable extends Expression {
    private final Token name;

    public Variable(Token name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();

        codeList.add("take [" + name.value() +"]");
        return codeList;
    }
    public Token getName() {
        return name;
    }
}