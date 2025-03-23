package org.example.simplejava.converters;

import javafx.scene.paint.Color;
import org.example.simplejava.ASTTree.ASTProgram;
import org.example.simplejava.helperObjects.AssemblyCode;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.CodeGenContext;

import java.util.ArrayList;

import static org.example.simplejava.helperObjects.MessageType.*;

/**
 * {@code @Author:} Thomas Lu
 * The Generator
 */

public class Generator extends Converter <ASTProgram,AssemblyCode>{

    public Generator(CompilationResult result) {
        super(result);
    }
    public AssemblyCode convert(ASTProgram program) {
        AssemblyCode code = new AssemblyCode();

        result.addMessage("<======================== Generator Start ========================>", Color.GREENYELLOW);

        // Information needed or be stored during code generation
        CodeGenContext context = new CodeGenContext();

        // Generated result
        ArrayList<String> codeList = new ArrayList<>(program.generate(context, result));

        StringBuilder codeBuilder = new StringBuilder();
        for (String instr : codeList) {
            codeBuilder.append(instr).append("\n");
        }

        code.setCode(codeBuilder.toString());

        code.setSymbolTable(context.getSymbolToAddressTable());

        code.setConstants(context.getConstantsList());

        result.addMessage(DEBUG,"Skipping the constant part\n"+ code.getCode());
        result.addMessage(INFO,"Total number of instructions (with labels): "+codeList.size());
        result.addMessage(INFO,"Maximum Address of Temp Variable: "+context.getMaxTempVariableAddress());
        result.addMessage("<======================== Generator End ========================>", Color.GREENYELLOW);
        return code;
    }
}
