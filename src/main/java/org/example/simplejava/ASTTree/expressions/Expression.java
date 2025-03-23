package org.example.simplejava.ASTTree.expressions;

import org.example.simplejava.ASTTree.ASTNode;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;

import java.util.ArrayList;

abstract public class Expression extends ASTNode {
    abstract public ArrayList<String> generate(CodeGenContext currentScope, CompilationResult result);
}