package org.example.simplejava.helperObjects;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code @Author:} Thomas Lu
 */

public class CompilationResult {
    private final List<CompilationMessage> messages = new ArrayList<>();
    private String outputProgram = "<Failed>";
    private boolean success = true;
    private boolean needExtendedInstructionSet=false;

    public void addMessage(String message, javafx.scene.paint.Color color) {

        System.out.println(message);

        message = completeEnterKey(message);
        messages.add(new CompilationMessage(message, color));
    }

    public void addMessage(MessageType messageType, String message) {

        if (messageType == MessageType.ERROR) {
            success = false;
        }
        System.out.println("[" + messageType + "]: " + message);

        message = completeEnterKey(message);
        switch (messageType) {
            case ERROR:
                messages.add(new CompilationMessage("[ERROR]", message, Color.RED));
                break;
            case WARNING:
                messages.add(new CompilationMessage("[WARNING]", message, Color.YELLOW));
                break;
            case INFO:
                messages.add(new CompilationMessage("[INFO]", message, Color.WHITE));
                break;
            case DEBUG:
                messages.add(new CompilationMessage("[DEBUG]", message, Color.DARKGREY));
                break;
            case SUCCESS:
                messages.add(new CompilationMessage("[SUCCESS]", message, Color.GREEN));
                break;
            default:
                messages.add(new CompilationMessage("[:(]", message, Color.RED));
                break;
        }
    }

    private String completeEnterKey(String str) {
        if (str.charAt(str.length() - 1) != '\n') {
            str += '\n';
        }
        return str;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    public boolean isNeedExtendedInstructionSet() {
        return needExtendedInstructionSet;
    }

    public void setNeedExtendedInstructionSet(boolean needExtendedInstructionSet) {
        this.needExtendedInstructionSet = needExtendedInstructionSet;
    }

    public List<CompilationMessage> getMessages() {
        return messages;
    }

    public String getOutputProgram() {
        return outputProgram;
    }

    public void setOutputProgram(String outputProgram) {
        this.outputProgram = outputProgram;
    }
}
