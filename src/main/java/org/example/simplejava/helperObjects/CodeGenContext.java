package org.example.simplejava.helperObjects;

import java.util.ArrayList;
import java.util.HashMap;

import static org.example.simplejava.helperObjects.MemoryAllocation.*;

public class CodeGenContext {

    private final HashMap<String, Integer> symbolToAddressTable;

    private final ArrayList<Integer> constantsList;

    private int tempVariableAddress;      //
    private int maxTempVariableAddress;

    private int globalVariableStartAddress = Global_VAR_StartAddress;

    private int labelCounter = 0;

    public CodeGenContext() {
        this.symbolToAddressTable = new HashMap<>();
        this.constantsList = new ArrayList<>();
        constantsList.add(0);
        constantsList.add(1);
        constantsList.add(PARAMETER_StartAddress);
        this.tempVariableAddress = TEMPVAR_StartAddress;
    }

    public String generateLabelID() {
        labelCounter++;
        return labelCounter + "";
    }

    public int getNextTempAddress() {
        tempVariableAddress++;
        maxTempVariableAddress = Math.max(maxTempVariableAddress, tempVariableAddress);
        return maxTempVariableAddress;
    }

    public void releaseTempAddress() {
        tempVariableAddress--;
    }

    public int getMaxTempVariableAddress() {
        return maxTempVariableAddress;
    }



    public int getNextVarAddress() {
        return globalVariableStartAddress++;
    }

    public void addSymbolAddressPair(String symbol, int address) {
        symbolToAddressTable.put(symbol, address);
    }

    public HashMap<String, Integer> getSymbolToAddressTable() {
        return symbolToAddressTable;
    }

    public ArrayList<Integer> getConstantsList() {
        return constantsList;
    }

}