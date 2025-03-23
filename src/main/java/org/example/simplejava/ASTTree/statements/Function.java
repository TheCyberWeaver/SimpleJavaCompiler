package org.example.simplejava.ASTTree.statements;

import org.example.simplejava.ASTTree.Parameter;
import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.MessageType;
import org.example.simplejava.helperObjects.Token;
import org.example.simplejava.tools.MsgLibrary;
import org.example.simplejava.tools.StringFormatter;

import java.util.ArrayList;

import static org.example.simplejava.helperObjects.MemoryAllocation.PARAMETER_StartAddress;
import static org.example.simplejava.tools.MsgLibrary.ASSIGN_MEM_TO_PAR;

public class Function extends Statement {
    public Token returnDataType;
    public Token name;
    public ArrayList<Parameter> parameters;
    public Block functionBody;

    public Function() {

    }

    public Function(Token returnDataType, Token name, ArrayList<Parameter> parameters, Block functionBody) {
        this.returnDataType = returnDataType;
        this.name = name;
        this.parameters = parameters;
        this.functionBody = functionBody;
    }

    @Override
    public String toString() {
        return functionBody.toString();
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();
        codeList.add(";"+name.value());

        //Receive Parameter
        if(parameters!=null) {
            for (int index = 0; index < parameters.size(); index++) {
                Parameter parameter = parameters.get(index);

                int address = PARAMETER_StartAddress+index;
                context.addSymbolAddressPair(parameter.name.value(), address);
                result.addMessage(MessageType.INFO, MsgLibrary.get(ASSIGN_MEM_TO_PAR,parameter.name.value(),address));
            }
        }

        codeList.addAll(StringFormatter.addTabToEachLine(functionBody.generate(context, result)));
        codeList.add("jmpi [RET]"); // Jump back to the position in the main method pointed by the RET address register.

        return codeList;
    }
}