package org.example.simplejava.ASTTree.expressions;

import org.example.simplejava.helperObjects.CodeGenContext;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.Token;

import java.util.ArrayList;

import static org.example.simplejava.helperObjects.MemoryAllocation.PARAMETER_StartAddress;

public class FunctionCall extends Expression {

    private final Token functionName;
    private final ArrayList<Expression> arguments;
    public FunctionCall(Token functionName, ArrayList<Expression> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        StringBuilder parameterStr = new StringBuilder();
        for(Expression arg : arguments) {
            parameterStr.append(arg.toString()).append(" | ");
        }

        return "<Function Call> "+ functionName + " <With Parameters> " + parameterStr;
    }

    @Override
    public ArrayList<String> generate(CodeGenContext context, CompilationResult result) {
        ArrayList<String> codeList = new ArrayList<>();

        result.setNeedExtendedInstructionSet(true); // Since instruction "savi" is needed.

        // Transfer parameters: Store the parameters at the location pointed to by the parameter register.
        for(Expression arg : arguments) {
            codeList.addAll(arg.generate(context, result));
            codeList.add("savi [PARAMETERS]");
            codeList.add("inc [PARAMETERS]");
        }

        // Reset Parameter Pointer
        codeList.add("take ["+PARAMETER_StartAddress+"]");
        codeList.add("save [PARAMETERS]");

        // The line number where the program continues running after the function returns is stored in the RET address register.
        codeList.add("take [THIS_LINE_PLUS_THREE]");
        codeList.add("save [RET]");

        // Jump into the function
        codeList.add("jmp ["+ functionName.value()+"]");

        // take the function returned value
        codeList.add("take [RET_VALUE]");

        return codeList;
    }

    public Token getFunctionName() {
        return functionName;
    }
}