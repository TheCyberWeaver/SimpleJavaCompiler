package org.example.simplejava.tools;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.example.simplejava.converters.Assembly2Johnny;

/***
 * Author: ChatGPT o3-mini-high
 * Created Date: 2.23.2025
 */

public class QuickConverter extends Application {


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Quick CodeConverter");

        TextArea inputArea = new TextArea();
        inputArea.setPromptText("Paste the code to be converted here...");
        inputArea.setMaxWidth(300);

        TextArea outputArea = new TextArea();
        outputArea.setPromptText("The converted code will appear here...");
        outputArea.setEditable(false);
        outputArea.setMaxWidth(300);

        Button convertButton = new Button("Convert");
        convertButton.setOnAction(event -> {
            String inputText = inputArea.getText();
            String outputText = convertProgram(inputText);
            outputArea.setText(outputText);
        });

        HBox buttonBox = new HBox(10, convertButton);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(buttonBox);
        root.setLeft(inputArea);
        root.setRight(outputArea);
        root.setStyle("-fx-background-color: #2B2B2B;");
        BorderPane.setMargin(inputArea, new Insets(10));
        BorderPane.setMargin(outputArea, new Insets(10));

        Scene scene = new Scene(root, 637, 450);
        // Apply JMetro style (choose between Style.LIGHT and Style.DARK)
        new JMetro(scene, Style.DARK);

        primaryStage.setMaxWidth(650);
        primaryStage.setMinWidth(650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private String convertProgram(String inputText) {
        Assembly2Johnny asm2Johnny = new Assembly2Johnny();
        StringBuilder[] codeStr=asm2Johnny.convertStrCode(inputText);

        StringBuilder outputText=new StringBuilder();
        for(int i=0;i<codeStr.length;i++){
            outputText.append(codeStr[i].toString());
        }
        return outputText.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
