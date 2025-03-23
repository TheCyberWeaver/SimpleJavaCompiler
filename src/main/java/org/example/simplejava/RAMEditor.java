package org.example.simplejava;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/***
 * Author: ChatGPT o3-mini-high
 * Created Date: 3.5.2025
 */

public class RAMEditor extends Application {

    // Total number of memory cells (1000 cells, indexed 0-999)
    private static final int TOTAL_CELLS = 1000;
    // Number of cells per row (10 cells per row)
    private static final int CELLS_PER_ROW = 10;
    // Total number of rows (100 rows)
    private static final int TOTAL_ROWS = TOTAL_CELLS / CELLS_PER_ROW;

    // List of memory rows (each row contains 10 memory cells)
    private List<MemoryRow> memoryRows = new ArrayList<>();
    // Global list of memory cells (indexed 0 to 999) for easy access
    private List<MemoryCell> globalCells = new ArrayList<>(TOTAL_CELLS);
    // Virtualized ListView for memory rows
    private ListView<MemoryRow> listView;

    // List of monitor components
    private List<MonitorComponent> monitors = new ArrayList<>();
    // Container for monitor components in the right pane
    private VBox monitorContainer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // -------------------------------
        // Create MenuBar with File menu items
        // -------------------------------
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(openItem, saveItem, new SeparatorMenuItem(), exitItem);
        menuBar.getMenus().add(fileMenu);

        // Set actions for menu items
        openItem.setOnAction(e -> openFile(primaryStage));
        saveItem.setOnAction(e -> saveFile(primaryStage));
        exitItem.setOnAction(e -> primaryStage.close());

        // -------------------------------
        // Create the left memory area using ListView for better scrolling performance.
        // Each item in the ListView represents a row (MemoryRow) that contains 10 memory cells.
        // -------------------------------
        listView = new ListView<>();
        ObservableList<MemoryRow> rows = FXCollections.observableArrayList();
        for (int row = 0; row < TOTAL_ROWS; row++) {
            MemoryRow memoryRow = new MemoryRow(row);
            memoryRows.add(memoryRow);
            rows.add(memoryRow);
        }
        listView.setItems(rows);
        // Set custom cell factory so that each MemoryRow is rendered properly.
        listView.setCellFactory(param -> new ListCell<MemoryRow>() {
            @Override
            protected void updateItem(MemoryRow item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });

        // -------------------------------
        // Create the right monitor area.
        // Users can add monitor components, each with a memory range definition and an output area.
        // -------------------------------
        // Button to add a new monitor component
        Button addMonitorButton = new Button("Add Monitor");
        addMonitorButton.setOnAction(e -> {
            MonitorComponent monitor = new MonitorComponent(0, 0);
            monitors.add(monitor);
            monitorContainer.getChildren().add(monitor);
            monitor.updateOutput();
        });

        // Container for monitor components (vertically arranged)
        monitorContainer = new VBox(10);
        monitorContainer.setPadding(new Insets(10));

        // Add two default monitors: one for range 700-720 and one for range 999-980
        MonitorComponent monitor1 = new MonitorComponent(700, 720);
        MonitorComponent monitor2 = new MonitorComponent(999, 980);
        monitors.add(monitor1);
        monitors.add(monitor2);
        monitorContainer.getChildren().addAll(monitor1, monitor2);

        // Wrap the monitor container in a ScrollPane
        ScrollPane monitorScrollPane = new ScrollPane(monitorContainer);
        monitorScrollPane.setFitToWidth(true);

        // Create a VBox for the right pane that includes the Add Monitor button and the monitor area
        VBox monitorPanel = new VBox(10, addMonitorButton, monitorScrollPane);
        monitorPanel.setPadding(new Insets(10));
        monitorPanel.setPrefWidth(300);

        // -------------------------------
        // Use a SplitPane to display the left memory area and right monitor area side by side.
        // -------------------------------
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(listView, monitorPanel);
        splitPane.setDividerPositions(0.7);

        // -------------------------------
        // Set up the main layout using a BorderPane
        // -------------------------------
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(splitPane);

        Scene scene = new Scene(root, 1230, 800);
        // Apply JMetro style (choose between Style.LIGHT and Style.DARK)
        new JMetro(scene, Style.DARK);
        primaryStage.setTitle("RAM Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Opens a .ram file, pads each line with leading zeros if it has fewer than 5 numbers,
     * and loads the data into the memory cells.
     * Supports lines that are either a 5-digit string (e.g., "00000") or 5 separate numbers (e.g., "0 0 0 0 0").
     *
     * @param stage the current Stage used to display the FileChooser.
     */
    private void openFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open RAM File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RAM files", "*.ram"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineIndex = 0;
                while ((line = reader.readLine()) != null && lineIndex < TOTAL_CELLS) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String instruction = "";
                    String data = "";

                    // Check if the line contains whitespace
                    if (line.contains(" ")) {
                        // Split the line into parts
                        String[] parts = line.split("\\s+");
                        // If there are fewer than 5 parts, pad with zeros at the front
                        if (parts.length < 5) {
                            int missing = 5 - parts.length;
                            String[] newParts = new String[5];
                            // Pad missing parts with "0"
                            for (int i = 0; i < missing; i++) {
                                newParts[i] = "0";
                            }
                            // Copy existing parts after the padded zeros
                            for (int i = 0; i < parts.length; i++) {
                                newParts[missing + i] = parts[i];
                            }
                            parts = newParts;
                        }
                        if (parts.length != 5) {
                            lineIndex++;
                            continue;
                        }
                        // Concatenate first two numbers as instruction, last three as data
                        instruction = parts[0] + parts[1];
                        data = parts[2] + parts[3] + parts[4];
                    } else {
                        // Single token (assumed to be a continuous string of digits)
                        if (line.length() < 5) {
                            // Pad with leading zeros to make 5 digits
                            line = String.format("%5s", line).replace(' ', '0');
                        }
                        if (line.length() != 5) {
                            lineIndex++;
                            continue;
                        }
                        instruction = line.substring(0, 2);
                        data = line.substring(2);
                    }
                    // Calculate row and column from line index
                    int rowIndex = lineIndex / CELLS_PER_ROW;
                    int colIndex = lineIndex % CELLS_PER_ROW;
                    MemoryRow row = memoryRows.get(rowIndex);
                    MemoryCell cell = row.getCells().get(colIndex);
                    cell.setInstruction(instruction);
                    cell.setData(data);
                    lineIndex++;
                }
                updateAllMonitors();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to open file: " + ex.getMessage());
            }
        }
    }

    /**
     * Saves the current memory cell data to a .ram file.
     * Each cell's instruction and data are padded with leading zeros if necessary,
     * and each digit is written separated by a space.
     *
     * @param stage the current Stage used to display the FileChooser.
     */
    private void saveFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save RAM File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RAM files", "*.ram"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Iterate over all memory cells (globalCells list holds them in order)
                for (MemoryCell cell : globalCells) {
                    String instruction = cell.getInstruction();
                    String data = cell.getData();
                    // Pad with leading zeros if needed
                    instruction = String.format("%2s", instruction).replace(' ', '0');
                    data = String.format("%3s", data).replace(' ', '0');
                    StringBuilder sb = new StringBuilder();
                    for (char c : (instruction + data).toCharArray()) {
                        sb.append(c).append(" ");
                    }
                    writer.println(sb.toString().trim());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to save file: " + ex.getMessage());
            }
        }
    }

    /**
     * Iterates over all monitor components and triggers an update to their outputs.
     */
    private void updateAllMonitors() {
        for (MonitorComponent monitor : monitors) {
            monitor.updateOutput();
        }
    }

    /**
     * Displays an alert dialog with the specified title and message.
     *
     * @param title   the title of the alert.
     * @param message the alert message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===============================
    // Inner class: MemoryCell
    // ===============================
    /**
     * Represents a single memory cell.
     * Each cell displays:
     * - A label for the cell's column number (0-9)
     * - A TextField for a 2-digit instruction (default "00")
     * - A TextField for a 3-digit data (default "000")
     */
    private class MemoryCell extends VBox {
        private Label cellLabel;
        private TextField instructionField;
        private TextField dataField;

        public MemoryCell(int rowIndex, int colIndex) {
            // Label to show the cell (column) index
            cellLabel = new Label(String.valueOf(colIndex));
            cellLabel.setStyle("-fx-font-weight: bold;");

            // Instruction field with default value "00"
            instructionField = new TextField("00");
            instructionField.setPromptText("Instruction (2 digits)");
            instructionField.setPrefColumnCount(4);
            // Restrict input to at most 2 digits
            instructionField.textProperty().addListener((obs, oldText, newText) -> {
                if (!newText.matches("\\d{0,2}")) {
                    instructionField.setText(oldText);
                }
            });

            // Data field with default value "000"
            dataField = new TextField("000");
            dataField.setPromptText("Data (3 digits)");
            dataField.setPrefColumnCount(4);
            // Restrict input to at most 3 digits and update monitors on change
            dataField.textProperty().addListener((obs, oldText, newText) -> {
                if (!newText.matches("\\d{0,3}")) {
                    dataField.setText(oldText);
                } else {
                    updateAllMonitors();
                }
            });

            this.setSpacing(2);
            this.setPadding(new Insets(5));
            this.setStyle("-fx-border-color: gray; -fx-border-width: 1;");
            this.getChildren().addAll(cellLabel, instructionField, dataField);
        }

        public String getInstruction() {
            return instructionField.getText().trim();
        }

        public String getData() {
            return dataField.getText().trim();
        }

        public void setInstruction(String instruction) {
            instructionField.setText(instruction);
        }

        public void setData(String data) {
            dataField.setText(data);
        }
    }

    // ===============================
    // Inner class: MemoryRow
    // ===============================
    /**
     * Represents a row of memory cells.
     * Each row starts with a label showing the row number, followed by 10 memory cells.
     */
    private class MemoryRow extends HBox {
        private Label rowLabel;
        private List<MemoryCell> cells = new ArrayList<>();
        private int rowIndex;

        public MemoryRow(int rowIndex) {
            this.rowIndex = rowIndex;
            // Label for the row number
            rowLabel = new Label("Row " + rowIndex);
            rowLabel.setPrefWidth(60);
            rowLabel.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-alignment: center;");
            this.setSpacing(5);
            this.setPadding(new Insets(5));
            this.getChildren().add(rowLabel);

            // Create 10 memory cells for this row
            for (int col = 0; col < CELLS_PER_ROW; col++) {
                MemoryCell cell = new MemoryCell(rowIndex, col);
                cells.add(cell);
                // Add cell to the global list (ensuring correct global ordering)
                globalCells.add(cell);
                this.getChildren().add(cell);
            }
        }

        public List<MemoryCell> getCells() {
            return cells;
        }
    }

    // ===============================
    // Inner class: MonitorComponent
    // ===============================
    /**
     * Represents a monitor component that displays memory data for a user-defined range.
     * The user can set lower and upper bounds (0-999) via text fields.
     * The component displays, in real time, the data of memory cells within that range,
     * with data separated by spaces. If the left bound is greater than the right bound,
     * the output is displayed in descending order.
     * A delete button allows removal of this monitor component.
     */
    private class MonitorComponent extends VBox {
        private TextField lowerBoundField;
        private TextField upperBoundField;
        private TextArea outputArea;
        private Button deleteButton;

        public MonitorComponent(int lowerBound, int upperBound) {
            // Create input fields for lower and upper bounds
            lowerBoundField = new TextField(String.valueOf(lowerBound));
            lowerBoundField.setPrefColumnCount(4);
            upperBoundField = new TextField(String.valueOf(upperBound));
            upperBoundField.setPrefColumnCount(4);

            // When bounds change, update the output
            lowerBoundField.textProperty().addListener((obs, oldText, newText) -> updateOutput());
            upperBoundField.textProperty().addListener((obs, oldText, newText) -> updateOutput());

            // Create a delete button to remove this monitor component
            deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                monitors.remove(this);
                monitorContainer.getChildren().remove(this);
            });

            // Create an HBox to hold the range input fields and the delete button
            HBox rangeBox = new HBox(5);
            rangeBox.getChildren().addAll(new Label("Range:"), new Label("From:"), lowerBoundField,
                    new Label("To:"), upperBoundField, deleteButton);

            // Create a non-editable TextArea for output and double its height (from 60 to 120)
            outputArea = new TextArea();
            outputArea.setEditable(false);
            outputArea.setWrapText(true);
            outputArea.setPrefHeight(120);

            this.setSpacing(5);
            this.setPadding(new Insets(5));
            this.setStyle("-fx-border-color: blue; -fx-border-width: 1;");
            this.getChildren().addAll(rangeBox, outputArea);

            // Initial update
            updateOutput();
        }

        /**
         * Updates the output area with memory cell data for the specified range.
         * Data is read from the globalCells list.
         */
        public void updateOutput() {
            int lower, upper;
            try {
                lower = Integer.parseInt(lowerBoundField.getText());
                upper = Integer.parseInt(upperBoundField.getText());
            } catch (NumberFormatException e) {
                outputArea.setText("Invalid range");
                return;
            }
            // Clamp values within valid indices 0 to 999
            if (lower < 0) lower = 0;
            if (upper < 0) upper = 0;
            if (lower > TOTAL_CELLS - 1) lower = TOTAL_CELLS - 1;
            if (upper > TOTAL_CELLS - 1) upper = TOTAL_CELLS - 1;
            StringBuilder sb = new StringBuilder();
            if (lower <= upper) {
                for (int i = lower; i <= upper; i++) {
                    sb.append(globalCells.get(i).getData()).append(" ");
                }
            } else {
                for (int i = lower; i >= upper; i--) {
                    sb.append(globalCells.get(i).getData()).append(" ");
                }
            }
            outputArea.setText(sb.toString().trim());
        }
    }
}
