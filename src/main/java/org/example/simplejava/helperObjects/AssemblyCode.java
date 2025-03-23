package org.example.simplejava.helperObjects;

import java.util.ArrayList;
import java.util.HashMap;

public class AssemblyCode {
    private String code;
    private HashMap<String, Integer> symbolTable;
    private ArrayList<Integer> constants;

    public AssemblyCode() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public HashMap<String, Integer> getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(HashMap<String, Integer> symbolTable) {
        this.symbolTable = symbolTable;
    }

    public ArrayList<Integer> getConstants() {
        return constants;
    }

    public void setConstants(ArrayList<Integer> constants) {
        this.constants = constants;
    }

    public void addConstant(int value) {
        constants.add(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(code);
        sb.append(";Constant List: \n");
        for (Integer constant : constants) {
            sb.append(constant).append("\n");
        }
        return sb.toString();
    }
}
