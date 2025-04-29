package org.example.sbsk_v2_slavemodule_card_camera;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.sbsk_v2_slavemodule_card_camera.scterminal.CardInteraction;
import org.example.sbsk_v2_slavemodule_card_camera.scterminal.Scterminal;
import org.example.sbsk_v2_slavemodule_card_camera.utils.JsonUtils;

import javax.smartcardio.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardVerificationStageController {

    private static final Logger logger = LoggerFactory.getLogger(CardVerificationStageController.class);
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String PYTHON_SCRIPT_PATH = "C:\\Users\\acer\\Documents\\PythonFaceVerificationRegistrationModule\\UserRegistrationAndVerification\\userVerificationAndSavingAttendanceDataLoRaTransmite\\Verification\\userVerification.py";

    @FXML private Label statusLabel;
    @FXML private Label idNumberLabel;
    @FXML private Label nameLabel;
    @FXML private Label validUntilLabel;



    public void handleReadCard(ActionEvent actionEvent) {
        try {
            // First check if card reader is available
            if (!isReaderAvailable()) {
                showAlert(Alert.AlertType.ERROR, "Reader Error", "No card reader detected.");
                return;
            }

            // Check if card is present
            if (!Scterminal.isCardPresent()) {
                updateStatus("Please insert your card into the reader");
                return;
            }

            updateStatus("Reading card...");
            readCardWithDetailedControl();

        } catch (Exception e) {
            handleCardError(e);
        }
    }

    private void readCardWithDetailedControl() throws CardException,
            CardInteraction.InstructionFailedException,
            CardInteraction.ByteCastException, CardInteraction.TerminalNotFoundException {

        CardTerminals terminals = CardInteraction.getCardTerminals();
        CardTerminal terminal = terminals.list().get(0);
        Card card = CardInteraction.connectToCard(terminal);

        if (card == null) {
            updateStatus("Failed to connect to card");
            return;
        }

        try {
            CardChannel channel = card.getBasicChannel();
            Map<String, String> cardDataMap = CardInteraction.readFile(channel);

            // Get card data
            String idNumber = cardDataMap.get("IDNUMBER");
            String name = cardDataMap.get("NAME");
            String validUpto = cardDataMap.get("VALIDUPTO");

            Platform.runLater(() -> {
                // Update UI with card data
                idNumberLabel.setText(idNumber != null ? idNumber : "N/A");
                nameLabel.setText(name != null ? name : "N/A");
                validUntilLabel.setText(validUpto != null ? validUpto : "N/A");

                // Verify user against JSON file
                if (idNumber != null && name != null) {
                    try {
                        boolean isValid = JsonUtils.isValidUser(idNumber, name);
                        if (isValid) {
                            showValidationSuccessPopup();
                            updateStatus("Valid user verified");
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Invalid User",
                                    "User not found in system or data mismatch");
                            updateStatus("Invalid user detected");
                        }
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Validation Error",
                                "Error verifying user: " + e.getMessage());
                        updateStatus("Validation error occurred");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Data Error",
                            "Could not read complete user data from card");
                    updateStatus("Incomplete card data");
                }
            });

        } finally {
            // Ensure card is always disconnected
            CardInteraction.disconnectCard(card);
        }
    }



    private void executePythonScriptOnTop(String scriptPath) {
        try {
            // Use ProcessBuilder to start the Python script
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath);

            // This ensures the script runs in a new console window (Windows-specific)
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder.command("cmd", "/c", "start", "python", scriptPath);
            }

            // Start the process
            Process process = processBuilder.start();

            // Optional: Monitor the process output
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("Python Output: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to execute Python script: " + e.getMessage());
            });
        }
    }

    private boolean isReaderAvailable() {
        try {
            CardInteraction.getCardTerminals();
            return true;
        } catch (CardInteraction.TerminalNotFoundException | CardException e) {
            return false;
        }
    }

    private void updateStatus(String message) {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(message);
            }
        });
    }

    private void handleCardError(Exception e) {
        String errorMessage;

        if (e instanceof CardInteraction.TerminalNotFoundException) {
            errorMessage = "No card reader found";
        } else if (e instanceof CardException) {
            errorMessage = "Card communication error: " + e.getMessage();
        } else if (e instanceof CardInteraction.InstructionFailedException) {
            errorMessage = "Failed to read card data";
        } else if (e instanceof CardInteraction.ByteCastException) {
            errorMessage = "Error processing card data";
        } else {
            errorMessage = "Unexpected error: " + e.getMessage();
        }

        updateStatus(errorMessage);
        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", errorMessage));
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void showValidationSuccessPopup() {
        Alert alert = createValidationAlert();
        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent(buttonType -> {
            if (buttonType == ButtonType.CLOSE) {
                handlePythonScriptExecution();
                closeCurrentStage();
            }
        });
    }

    private Alert createValidationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Valid User");
        alert.setHeaderText("User Verification Successful");
        alert.setContentText("The user has been successfully verified.");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getButtonTypes().setAll(ButtonType.CLOSE);

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        alert.initOwner(stage);

        return alert;
    }

    private void handlePythonScriptExecution() {
        if (!validatePythonScriptPath()) {
            showAlert(Alert.AlertType.ERROR, "Configuration Error",
                    "Python script path is invalid or script doesn't exist");
            return;
        }

        executorService.submit(() -> {
            try {
                executePythonScript();
            } catch (ScriptExecutionException e) {
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Execution Error", e.getMessage()));
            }
        });
    }

    private boolean validatePythonScriptPath() {
        try {
            Path path = Paths.get(PYTHON_SCRIPT_PATH);
            return Files.exists(path) && Files.isReadable(path);
        } catch (SecurityException e) {
            logger.error("Security exception when validating Python script path", e);
            return false;
        }
    }

    private void executePythonScript() throws ScriptExecutionException {
        try {
            ProcessBuilder processBuilder = createProcessBuilder();
            Process process = processBuilder.start();
            logProcessOutput(process);

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new ScriptExecutionException(
                        String.format("Python script exited with error code: %d", exitCode));
            }

            logger.info("Python script executed successfully");
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptExecutionException("Failed to execute Python script: " + e.getMessage(), e);
        }
    }

    private ProcessBuilder createProcessBuilder() {
        ProcessBuilder processBuilder = new ProcessBuilder();

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder.command("cmd", "/c", "start", "python", PYTHON_SCRIPT_PATH);
        } else {
            processBuilder.command("python", PYTHON_SCRIPT_PATH);
        }

        // Configure environment if needed
        Map<String, String> env = processBuilder.environment();
        env.put("PYTHONUNBUFFERED", "1"); // For real-time output

        return processBuilder;
    }

    private void logProcessOutput(Process process) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("Python Output: {}", line);
                }
            } catch (IOException e) {
                logger.error("Error reading Python script output", e);
            }
        }).start();
    }

    private void closeCurrentStage() {
        Platform.runLater(() -> {
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.close();
        });
    }

    private static class ScriptExecutionException extends Exception {
        public ScriptExecutionException(String message) {
            super(message);
        }

        public ScriptExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}