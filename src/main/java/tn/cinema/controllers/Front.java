package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Front {

    @FXML
    private Button coursButton;

    @FXML
    private Button demandeSubButton;

    @FXML
    private Button filmsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button monCompteButton;

    @FXML
    private Button produitsButton;

    @FXML
    private Button publiciteSubButton;

    @FXML
    private Button publicitesButton;

    @FXML
    void filmsButtonClicked(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FrontFilm.fxml")); // Adjust path if needed
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();   // Get current window
            Scene scene = new Scene(root);
            stage.setScene(scene); // Set the new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // For debug
        }


    }

    @FXML
    void goToDemandeClient(ActionEvent event) {

    }

    @FXML
    void goToPubliciteClient(ActionEvent event) {

    }

    @FXML
    void logout(ActionEvent event) {

    }

    @FXML
    void toggleSubButtons(ActionEvent event) {

    }

}
