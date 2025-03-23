package org.example.simplejava.ASTTree;

import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.Token;

import java.util.ArrayList;

public class Parameter extends ASTNode {
    public Token datatype;
    public Token name;

    public Parameter(Token datatype, Token name) {
        this.datatype = datatype;
        this.name = name;
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        return null;
    }

    @Override
    public String toString() {
        return "";
    }
}