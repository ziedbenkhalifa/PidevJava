package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChoixG extends Dashboard {

    @FXML
    void GestionCommande(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherCommande.fxml")); // chemin FXML gestion commandes
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // tu peux aussi afficher une alerte ici
        }
    }

    @FXML
    void GestionProduit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherProduit.fxml")); // chemin FXML gestion produits
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // tu peux aussi afficher une alerte ici
        }
    }
}

