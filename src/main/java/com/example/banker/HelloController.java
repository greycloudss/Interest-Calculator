package com.example.banker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HelloController {
    /*@FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }*/

    @FXML
    private Button closeButton;

    @FXML
    public void handleCloseButton(ActionEvent actionEvent) {
        // Get the current stage (window)
        Stage stage = (Stage) closeButton.getScene().getWindow();
        // Close the stage
        stage.close();
    }
}