package org.example.simplejava;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.example.simplejava.helperObjects.CompilationMessage;
import org.example.simplejava.helperObjects.CompilationResult;
import org.example.simplejava.tools.MsgLibrary;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.example.simplejava.helperObjects.MessageType.*;
import static org.example.simplejava.tools.MsgLibrary.*;

/***
 * Author: ChatGPT o3-mini-high
 * Created Date: 2.18.2025
 */

public class SimpleJavaUI extends Application {
    public static int LOGGING_STYLE;
    // TextFlow to display messages with different colors.
    private TextFlow outputFlow;
    private BorderPane root;
    // Left TextArea for source code.
    private TextArea leftTextArea;
    // Right TextArea for compiled output (read-only).
    private final TextArea rightTextArea = new TextArea();
    // Label to display the currently loaded file name (left side).
    private Label currentFileLabel;
    // Current file loaded in the left editor.
    private File currentFile;
    // AutoSave toggle button.
    private ToggleButton autoSaveToggle;
    private ToggleButton kawaiiToggle;
    private ToggleButton showDebugToggle;
    // ComboBox for output language selection.
    private ComboBox<String> outputLanguageComboBox;
    // Additional controls for saving RAM output.
    private Button ramSaveButton;
    private Label ramFileLabel;
    private Button quickSaveButton;
    // The currently selected RAM file.
    private File ramCurrentFile;

    // Reference to the output ScrollPane for auto-scrolling.
    private ScrollPane outputScrollPane;

    private boolean hasShownPopUp=false;

    @Override
    public void start(Stage primaryStage) {
        // Create the main layout using BorderPane.
        root = new BorderPane();

        // Top: Toolbar with toggle buttons and compile button.
        ToolBar toolBar = createToolBar();
        root.setTop(toolBar);

        root.setStyle("-fx-background-color: #2B2B2B;");

        // Center: SplitPane with left and right editors.
        SplitPane centerPane = createCenterPane();
        root.setCenter(centerPane);

        // Bottom: Output area and credit label.
        VBox bottomPane = createBottomPane();
        root.setBottom(bottomPane);

        // Create the scene and apply JMetro styling.
        Scene scene = new Scene(root, 800, 750);
        new JMetro(scene, Style.DARK);

        primaryStage.setTitle("SimpleJava Compiler UI");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates the top toolbar containing toggle buttons for error reporting,
     * auto-save, out-of-order execution, and the compile button.
     *
     * @return the ToolBar.
     */
    private ToolBar createToolBar() {
        // Toggle buttons for various features.
        showDebugToggle = new ToggleButton("Show Debug");
        autoSaveToggle = new ToggleButton("AutoSave \uD83D\uDDAB");
        // Enable auto-save at startup.
        autoSaveToggle.setSelected(false);

        //ToggleButton outOfOrderToggle = new ToggleButton("Out-of-Order");
        kawaiiToggle = new ToggleButton("✨\uD83D\uDC96");
        setLOGGING_STYLE(kawaiiToggle.isSelected());
        kawaiiToggle.setOnAction(e -> setLOGGING_STYLE(kawaiiToggle.isSelected()));

        Button compileButton = new Button("Compile ▶");

        // Set action for compile button.
        compileButton.setOnAction(e -> compileCode());

        // Create the toolbar and add all buttons.
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(showDebugToggle, autoSaveToggle, kawaiiToggle, new Separator(), compileButton);
        return toolBar;
    }

    private void setLOGGING_STYLE(boolean b) {
        LOGGING_STYLE = b ? 1 : 0;
    }

    /**
     * Creates the center area containing two text editors:
     * the left editor for source code (with new, load, and save buttons)
     * and the right editor for compiled output.
     *
     * @return the SplitPane containing both editors.
     */
    private SplitPane createCenterPane() {
        // Left editor pane.
        VBox leftEditorPane = new VBox();
        leftEditorPane.setPadding(new Insets(5));
        leftEditorPane.setSpacing(5);

        // Label for the left editor.
        Label leftLabel = new Label("SimpleJava Source Code");

        // HBox for new, load, and save buttons.
        HBox leftButtonBox = new HBox();
        leftButtonBox.setSpacing(5);
        Button newButton = new Button("New ➕");
        Button loadButton = new Button("Load \uD83D\uDCC2");
        Button saveButton = new Button("Save \uD83D\uDDAB");

        // Action for New button: clear the source code editor.
        newButton.setOnAction(e -> {
            leftTextArea.clear();
            currentFile = null;
            currentFileLabel.setText("No file loaded");
        });

        // Action for Load button: open a file chooser and load file content.
        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("Java Files", "*.java")
            );
            File file = fileChooser.showOpenDialog(root.getScene().getWindow());
            if (file != null) {
                try {
                    String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    leftTextArea.setText(content);
                    currentFile = file;
                    currentFileLabel.setText(file.getName());
                } catch (IOException ex) {
                    appendOutput("Failed to load file: " + ex.getMessage() + "\n", Color.RED);
                }
            }
        });

        // Action for Save button.
        saveButton.setOnAction(e -> {
            if (currentFile != null) {
                try {
                    String content = leftTextArea.getText();
                    Files.write(currentFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
                    appendOutput("File saved successfully.\n", Color.WHITE);
                } catch (IOException ex) {
                    appendOutput("Failed to save file: " + ex.getMessage() + "\n", Color.RED);
                }
            } else if (!leftTextArea.getText().isEmpty()) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                        new FileChooser.ExtensionFilter("Java Files", "*.java")
                );
                File file = fileChooser.showSaveDialog(root.getScene().getWindow());
                if (file != null) {
                    try {
                        String content = leftTextArea.getText();
                        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
                        currentFile = file;
                        currentFileLabel.setText(file.getName());
                        appendOutput("File saved successfully.\n", Color.WHITE);
                    } catch (IOException ex) {
                        appendOutput("Failed to save file: " + ex.getMessage() + "\n", Color.RED);
                    }
                }
            } else {
                appendOutput("Editor is empty. Nothing to save.\n", Color.RED);
            }
        });
        // Label to display the currently loaded file name.
        currentFileLabel = new Label("No file loaded");

        leftButtonBox.getChildren().addAll(newButton, loadButton, saveButton, currentFileLabel);

        // TextArea for source code input.
        leftTextArea = new TextArea();
        VBox.setVgrow(leftTextArea, Priority.ALWAYS);

        leftEditorPane.getChildren().addAll(leftLabel, leftButtonBox, leftTextArea);

        // Right editor pane.
        VBox rightEditorPane = new VBox();
        rightEditorPane.setPadding(new Insets(5));
        rightEditorPane.setSpacing(5);

        // Label for the right editor.
        Label rightLabel = new Label("Compiled Code");

        // ComboBox for selecting the output language.
        outputLanguageComboBox = new ComboBox<>();
        outputLanguageComboBox.getItems().addAll("Tokens", "Tokens(Processed)", "AST Tree", "Johnny Assembly", "Johnny Code");
        outputLanguageComboBox.setValue("Johnny Code");
        outputLanguageComboBox.setMaxWidth(130);

        // Create HBox for extra RAM saving controls.
        HBox ramControlsBox = new HBox();
        ramControlsBox.setSpacing(5);
        ramSaveButton = new Button("Save");
        ramFileLabel = new Label("no file selected");
        quickSaveButton = new Button("Quick Save");
        quickSaveButton.setVisible(false);
        ramControlsBox.getChildren().addAll(ramSaveButton, quickSaveButton, ramFileLabel);

        // HBox that contains the ComboBox and the extra controls.
        HBox outputControlBox = new HBox();
        outputControlBox.setSpacing(5);
        outputControlBox.getChildren().addAll(outputLanguageComboBox, ramControlsBox);

        // Only show extra RAM controls if "Johnny Code" is selected.
        outputLanguageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean show = "Johnny Code".equals(newVal);
            ramControlsBox.setVisible(show);
        });
        // Set initial visibility.
        ramControlsBox.setVisible("Johnny Code".equals(outputLanguageComboBox.getValue()));

        // Event handler for "Save RAM" button.
        ramSaveButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save RAM File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RAM Files", "*.ram"));
            File file = fileChooser.showSaveDialog(root.getScene().getWindow());
            if (file != null) {
                try {
                    String content = rightTextArea.getText();
                    // Append .ram extension if not present.
                    if (!file.getName().toLowerCase().endsWith(".ram")) {
                        file = new File(file.getAbsolutePath() + ".ram");
                    }
                    Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
                    ramCurrentFile = file;
                    ramFileLabel.setText(file.getName());
                    quickSaveButton.setVisible(true);
                } catch (IOException ex) {
                    appendOutput("Failed to save RAM file: " + ex.getMessage() + "\n", Color.RED);
                }
            }
        });
        // Event handler for "Quick Save" button.
        quickSaveButton.setOnAction(e -> {
            if (ramCurrentFile != null) {
                try {
                    String content = rightTextArea.getText();
                    Files.write(ramCurrentFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
                    appendOutput("RAM file quick-saved successfully.\n", Color.WHITE);
                } catch (IOException ex) {
                    appendOutput("Quick save failed: " + ex.getMessage() + "\n", Color.RED);
                }
            }
        });

        rightTextArea.setEditable(false);
        VBox.setVgrow(rightTextArea, Priority.ALWAYS);

        rightEditorPane.getChildren().addAll(rightLabel, outputControlBox, rightTextArea);

        // Create a SplitPane to hold both editor panes.
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftEditorPane, rightEditorPane);
        splitPane.setDividerPositions(0.5);

        return splitPane;
    }

    /**
     * Creates the bottom area which contains the output area for messages
     * and a credit label at the bottom-right.
     *
     * @return the VBox containing the output area and credit label.
     */
    private VBox createBottomPane() {
        VBox bottomPane = new VBox();
        bottomPane.setPadding(new Insets(5));
        bottomPane.setSpacing(5);

        outputFlow = new TextFlow();
        // Store the ScrollPane reference for later use.
        outputScrollPane = new ScrollPane(outputFlow);
        outputScrollPane.setFitToWidth(true);
        outputScrollPane.setPrefHeight(300);

        appendOutput(MsgLibrary.get(WELCOME_MESSAGE), Color.WHITE);

        HBox creditBox = new HBox();
        creditBox.setPadding(new Insets(5));
        creditBox.setAlignment(Pos.CENTER_RIGHT);
        Label creditLabel = new Label("Thomas - Version 1.1");
        creditBox.getChildren().add(creditLabel);

        bottomPane.getChildren().addAll(outputScrollPane, creditBox);
        return bottomPane;
    }

    /**
     * Appends a message to the output area with the specified text color.
     *
     * @param message the message to append.
     * @param color   the color of the text.
     */
    private void appendOutput(String message, Color color) {
        Text text = new Text(message);
        text.setStyle("-fx-fill: " + toRgbCode(color) + ";");
        outputFlow.getChildren().add(text);
    }

    /**
     * Converts a Color to a CSS hex color string.
     *
     * @param color the Color.
     * @return the CSS hex color string.
     */
    private String toRgbCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Compiles the source code in the left editor using the Compiler.
     * Thomas Lu
     */
    private void compileCode() {
        String sourceCode = leftTextArea.getText();
        if (sourceCode.isEmpty()) {
            System.out.println("Source code is empty.");
        }
        // Create a new Compiler instance to compile the source code.
        Compiler compiler = new Compiler();
        CompilationResult result = compiler.compileWithTimeout(sourceCode, outputLanguageComboBox.getValue());

        if (result.isSuccess()) {
            result.addMessage(SUCCESS, MsgLibrary.get(COMPILATION_END));
        }
        else{
            result.addMessage(ERROR, MsgLibrary.get(CRITICAL_ERROR));
        }

        if(result.isNeedExtendedInstructionSet()&&!hasShownPopUp){
            Stage stage = new Stage();
            String serverFileUrl = "https://www.thomas-hub.com/sharing/extended%20micro%20code.zip";
            Platform.runLater(() -> WarningPopUp.showWarningDialog(serverFileUrl,stage));
            hasShownPopUp=true;
        }

        // Clear the output area.
        outputFlow.getChildren().clear();
        for (CompilationMessage msg : result.getMessages()) {
            if (!showDebugToggle.isSelected() && msg.getMessageType().equals("[DEBUG]")) {
                continue;
            }
            appendOutput(msg.getMessage(), msg.getColor());
        }
        // Display the final compiled program in the right editor.
        rightTextArea.setText(result.getOutputProgram());
        // Automatically scroll the output area to the bottom.
        outputScrollPane.setVvalue(1.0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
