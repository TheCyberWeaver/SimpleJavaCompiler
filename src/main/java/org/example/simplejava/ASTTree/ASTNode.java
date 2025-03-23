package org.example.simplejava.ASTTree;

import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;

import java.util.ArrayList;

abstract public class ASTNode {
    abstract public ArrayList<String> generate(CodeGenContext context, CompilationResult result);
    @Override
    public abstract String toString();
}
