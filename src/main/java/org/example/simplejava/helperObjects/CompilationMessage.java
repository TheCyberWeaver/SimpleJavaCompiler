package org.example.simplejava.helperObjects;

import javafx.scene.paint.Color;

/**
 * {@code @Author:} Thomas Lu
 */

public class CompilationMessage {
    private String messageType="";
    private final String message;
    private final Color color;

    public CompilationMessage(String message, Color color) {
        this.messageType = "";
        this.message = message;
        this.color = color;
    }

    public CompilationMessage(String messageType, String message, Color color) {
        this.messageType = messageType;
        this.message = message;
        this.color = color;
    }

    public String getMessage() {
        if (messageType.equals("")) {
            return message;
        }
        return messageType + ": " + message;
    }

    public String getMessageType() {
        return messageType;
    }

    public Color getColor() {
        return color;
    }
}
