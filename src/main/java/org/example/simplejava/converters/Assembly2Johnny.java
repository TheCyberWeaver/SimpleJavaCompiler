

package org.example.simplejava.converters;

import javafx.scene.paint.Color;
import org.example.simplejava.helperObjects.AssemblyCode;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.helperObjects.MessageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.simplejava.Compiler.RAM_SIZE;
import static org.example.simplejava.helperObjects.MemoryAllocation.*;
import static org.example.simplejava.helperObjects.MessageType.*;

/**
 * {@code @Author:} Thomas Lu
 * Converts the program by:
 * 1. Replace labels with the actual line number
 * 2. Convert the program in to ram file format
 */

public class Assembly2Johnny extends Converter <AssemblyCode,String[]>
{

    // Pattern to match label definition: ;label (label is any non-whitespace sequence)
    private static final Pattern LABEL_DEF_PATTERN = Pattern.compile(";([^\\s]+)");
    // Pattern to match label reference: [label]
    private static final Pattern LABEL_REF_PATTERN = Pattern.compile("\\[([^]]+)]");

    private static final Map<String,String> instructionMap = createJohnnyInstructionSet();

    private AssemblyCode code;


    private final String[] ram = new String[RAM_SIZE];
    {
        Arrays.fill(ram, "00000");  //initialize ram with zeros
    }

    public Assembly2Johnny(){
        super(new CompilationResult());
    }
    public Assembly2Johnny(CompilationResult result){
        super(result);
    }
    public String[] convert(AssemblyCode input){
        this.code=input;

        result.addMessage("<======================== Assembly To RAM Code Start ========================>", Color.BISQUE);
        StringBuilder[] codeStr=convertStrCode(code.getCode());

        for(int i=0;i<codeStr.length;i++){
            ram[i]=codeStr[i].toString();
        }


        // add special constants
        ram[ERROR_DefaultAddress]="10000"; // default to hlt when error occurs
        ram[PARAMETER_PointerAddress]="00"+PARAMETER_StartAddress;  // Initialize the parameter pointer
        ram[OUTPUT_PointerAddress]="00"+OUTPUT_StartAddress;    // Initialize the output pointer

        // fill zeros before lines that store constants
        for(int i=0;i<code.getConstants().size();i++){
            ram[CONSTANT_StartAddress +i]=String.format("%05d", code.getConstants().get(i));
        }

        //Logging
        result.addMessage(INFO,"Total number of Instructions: "+codeStr.length);
        result.addMessage(INFO,"Constants stored starting from Address: "+ CONSTANT_StartAddress);
        result.addMessage(INFO,"Total number of constants:  "+code.getConstants().size());
        result.addMessage("<======================== Assembly To RAM Code End ========================>", Color.BISQUE);

        return ram;
    }
    public StringBuilder[] convertStrCode(String input) {

        ArrayList<String> lines = new ArrayList<>(Arrays.asList(input.split("\n")));

        // Zero pass (Preprocess):  delete tabs && remove empty lines

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String trimmed = line.replaceAll("^[ \\t]+|[ \\t]+$", "");
            lines.set(i, trimmed);
            if(lines.get(i).isEmpty()){
                lines.remove(i);
                i-=1;
            }
            //System.out.println(trimmed);
        }

        // First pass (Preprocess): rearrange lines that have only one label

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if(line.charAt(0)==';'){
                // Move the label to the end of the next line
                lines.remove(i);

                if (i < lines.size()) {
                    lines.set(i,lines.get(i)+" "+line);
                    i--;
                } else {
                    lines.add(";"+line);   // If last line is an empty line with only one label
                    System.out.println("Warning: Last Line is a label");
                }
            }
        }

        //DEBUG
        for (int i = 0; i < lines.size(); i++) {
            result.addMessage(DEBUG,i+": "+lines.get(i));
        }


        // Second pass: find label definitions and remove ';label'

        // Processed lines are stored in an array of StringBuilders
        StringBuilder[] processedLines = new StringBuilder[lines.size()];

        // Record label -> line number (original line number starting from 1)
        Map<String, Integer> labelLineMap = new HashMap<>();

        for (int i = 0; i < lines.size() ;i++) {
            String line = lines.get(i);
            Matcher matcher = LABEL_DEF_PATTERN.matcher(line);
            StringBuilder sb = new StringBuilder();

            while (matcher.find()) {
                String label = matcher.group(1);
                labelLineMap.putIfAbsent(label, i);
                result.addMessage(DEBUG,"Found Label at line: "+i+" : "+label);
                matcher.appendReplacement(sb, "");
            }
            matcher.appendTail(sb);
            processedLines[i] = new StringBuilder(sb.toString());
        }

        // 2.5 pass: process special labels THIS_LINE_PLUS_THREE

        for (int i = 0; i < processedLines.length; i++) {
            String line = processedLines[i].toString();
            Matcher refMatcher = LABEL_REF_PATTERN.matcher(line);
            StringBuilder sb = new StringBuilder();
            while (refMatcher.find()) {
                String label = refMatcher.group(1);
                if(label.equals("THIS_LINE_PLUS_THREE")){
                    refMatcher.appendReplacement(sb,  "["+(i+3)+"]");
                    code.addConstant(i+3);

                    result.addMessage(INFO,"Special label THIS_LINE_PLUS_THREE at line: "+i+" : "+line);
                }
            }
            refMatcher.appendTail(sb);
            processedLines[i] = new StringBuilder(sb.toString());
        }

        // extra Labels
        addPredefinedLabel(labelLineMap);

        // Third pass: replace all occurrences of [label] with  the line number (Code Lo)
        for (int i = 0; i < processedLines.length; i++) {
            String line = processedLines[i].toString();
            Matcher refMatcher = LABEL_REF_PATTERN.matcher(line);
            StringBuilder sb = new StringBuilder();
            while (refMatcher.find()) {
                String label = refMatcher.group(1);

                Integer targetLine = labelLineMap.get(label);
                if (targetLine != null) {
                    // Replace with ';label' concatenated with the original line number
                    refMatcher.appendReplacement(sb,  ""+targetLine);
                } else {
                    refMatcher.appendReplacement(sb,  ERROR_DefaultAddress+"");
                    result.addMessage(MessageType.ERROR,"Error: label without definition, default to ERROR_DefaultAddress: " + line);
                    // If no definition is found, keep the original text unchanged
                    //refMatcher.appendReplacement(sb, refMatcher.group(0));
                }
            }
            refMatcher.appendTail(sb);
            processedLines[i] = new StringBuilder(sb.toString());
        }

        //Forth pass: replace the instructions with numbers(Code Hi)
        for (int i = 0; i < processedLines.length; i++) {
            String line = processedLines[i].toString();
            if(line.isEmpty()){
                processedLines[i]=new StringBuilder("00000");
            }
            else if (line.matches("\\d{1,3}")) {
                processedLines[i]=new StringBuilder(String.format("%05d", Integer.parseInt(line)));
            }
            else{
                processedLines[i]=new StringBuilder(replaceInstructionWithCode(line));
            }

        }

        return processedLines;
    }

    private void addPredefinedLabel(Map<String, Integer> labelLineMap) {

        labelLineMap.putAll(code.getSymbolTable());
        labelLineMap.put("RET",RETURN_PointerAddress);
        labelLineMap.put("RET_VALUE", FUNCTION_Result_Address);
        labelLineMap.put("ERR",ERROR_DefaultAddress);
        labelLineMap.put("OUT", OUTPUT_PointerAddress);
        labelLineMap.put("PARAMETERS", PARAMETER_PointerAddress);

        // Add constants as predefined symbols
        for(int i=0;i<code.getConstants().size();i++){
            labelLineMap.putIfAbsent(code.getConstants().get(i).toString(), CONSTANT_StartAddress +i);
        }

    }

    private static Map<String, String> createJohnnyInstructionSet (){
        Map<String, String> johnnyInstructionSet = new HashMap<>();
        // johnnyInstructionSet.putIfAbsent("", "00");
        johnnyInstructionSet.putIfAbsent("take", "01");
        johnnyInstructionSet.putIfAbsent("add", "02");
        johnnyInstructionSet.putIfAbsent("sub", "03");
        johnnyInstructionSet.putIfAbsent("save", "04");
        johnnyInstructionSet.putIfAbsent("jmp", "05");
        johnnyInstructionSet.putIfAbsent("tst", "06");
        johnnyInstructionSet.putIfAbsent("inc", "07");
        johnnyInstructionSet.putIfAbsent("dec", "08");
        johnnyInstructionSet.putIfAbsent("null", "09");
        johnnyInstructionSet.putIfAbsent("hlt", "10");

        //Extended Instruction Set
        johnnyInstructionSet.putIfAbsent("savi", "11");
        johnnyInstructionSet.putIfAbsent("jmpi", "12");

        return johnnyInstructionSet;
    }

    /**
     * @Author ChatGPT o3-mini
     * Transforms an input instruction string into its corresponding code.
     * The method replaces the instruction at the beginning of the string with its mapped code
     * and pads the remaining numeric operand to three digits (defaults to 000 if absent).
     */
    public String replaceInstructionWithCode(String input) {
        // Trim the input and split by whitespace.
        input = input.trim();
        String[] parts = input.split("\\s+");

        // Get the instruction code from the map.
        String instruction = parts[0];
        String code = instructionMap.get(instruction);

        // If no mapping exists, you could handle the error here. For now, we assume mapping exists.
        if (code == null) {
            result.addMessage(MessageType.WARNING,"Invalid instruction: " + instruction);
            return "10000";
        }

        // If there is an operand, parse it; otherwise default to 0.
        int operand = 0;
        if (parts.length > 1 && !parts[1].isEmpty() ) {
            if( !Character.isDigit(parts[1].charAt(0)) || Integer.parseInt(parts[1])>999 ){
                result.addMessage(MessageType.WARNING,"Invalid Operand: " + instruction);
                return "10000";
            }
            operand = Integer.parseInt(parts[1]);
        }

        // Format the operand as a three-digit number with leading zeros.
        String formattedOperand = String.format("%03d", operand);

        return code + formattedOperand;
    }
}
