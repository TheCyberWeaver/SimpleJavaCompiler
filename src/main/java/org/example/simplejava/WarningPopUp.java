package org.example.simplejava;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

/***
 * Author: ChatGPT o3-mini-high
 * Created Date: 3.19.2025
 */

public class WarningPopUp {
    // Static method to display the warning dialog.
    public static void showWarningDialog(String serverFileUrl, Stage ownerStage) {
        // Create custom button types.
        ButtonType downloadButton = new ButtonType("Download", ButtonData.OK_DONE);
        ButtonType ignoreButton = new ButtonType("Ignore", ButtonData.CANCEL_CLOSE);

        // Create an Alert of type WARNING with a custom message.
        Alert alert = new Alert(AlertType.WARNING,
                "IO and function calling are only supported through extended instruction set\n" +
                        "You can click the Download button below to download the file.",
                downloadButton, ignoreButton);
        alert.setTitle("Warning");
        alert.setHeaderText("Extended instruction set needed");

        // Show the dialog and wait for the user's response.
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == downloadButton) {
            // Open a file chooser to let the user select a save location.
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save ZIP File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Files", "*.zip"));
            fileChooser.setInitialFileName("extendedMicroCode.zip");
            File destination = fileChooser.showSaveDialog(ownerStage);
            if (destination != null) {
                // Download the file from the server.
                try {
                    downloadFile(serverFileUrl, destination);
                    System.out.println("Download successful: " + destination.getAbsolutePath());
                } catch (IOException ex) {
                    System.out.println("Download failed: " + ex.getMessage());
                }
            }
        } else {
            System.out.println("Ignore button clicked.");
        }
    }
    /**
     * Downloads a file from the specified URL and writes it to the destination file.
     *
     * @param fileUrl     the URL of the file to download.
     * @param destination the file where the downloaded content will be saved.
     * @throws IOException if an I/O error occurs.
     */
    public static void downloadFile(String fileUrl, File destination) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Open input stream from the HTTP connection.
            InputStream inputStream = httpConn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(destination);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
        } else {
            throw new IOException("Server returned non-OK response code: " + responseCode);
        }
        httpConn.disconnect();
    }
}

