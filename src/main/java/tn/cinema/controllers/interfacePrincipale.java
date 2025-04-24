package tn.cinema.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class interfacePrincipale {
    @FXML
    void handleGestionSalle(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // pour voir l’erreur s’il y en a une
        }
    }
}
