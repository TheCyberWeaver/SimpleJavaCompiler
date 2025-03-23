package org.example.simplejava.ASTTree.statements;

import org.example.simplejava.ASTTree.expressions.Expression;
import org.example.simplejava.ASTTree.expressions.Variable;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.MessageType;
import org.example.simplejava.helperObjects.Token;
import org.example.simplejava.tools.MsgLibrary;

import java.util.ArrayList;

import static org.example.simplejava.tools.MsgLibrary.ASSIGN_MEM_TO_VAR;

public class VarDeclaration extends Statement {
    private final Token datatype;
    private final Variable variable;

    private Expression initializer; //Optional

    public VarDeclaration(Token datatype, Variable variable) {
        this.datatype = datatype;
        this.variable = variable;
    }
    public VarDeclaration(Token datatype, Variable variable, Expression initializer) {
        this.datatype = datatype;
        this.variable = variable;
        this.initializer = initializer;
    }

    @Override
    public String toString() {
        if(initializer == null){
            return "<VarDeclaration> "+datatype+" "+ variable;
        }
        return "<VarDeclaration> "+datatype+" "+ variable +" <Initialized With> "+ initializer;
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();

        int address = context.getNextVarAddress();
        context.addSymbolAddressPair(variable.getName().value(), context.getNextVarAddress());

        result.addMessage(MessageType.INFO, MsgLibrary.get(ASSIGN_MEM_TO_VAR,variable.getName().value(),address));

        // initialize the variable (optional)
        if(initializer != null) {
            codeList.addAll(initializer.generate(context,result));
            codeList.add("save [" + variable.getName().value()+"]");
        }

        //codeList.add("null [" + variable.name.getValue()+"]"); // initialize with 0

        return codeList;
    }
}