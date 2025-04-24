package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.cinema.entities.Equipement;
import tn.cinema.services.EquipementService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListeEquipement extends Dashboard{

    @FXML
    private ListView<Equipement> listViewEquipements;

    @FXML
    private Button btnAjouter;

    @FXML
    public void initialize() {
        if (btnAjouter == null) {
            System.out.println("⚠️ btnAjouter n'est pas lié. Vérifie le fx:id dans le FXML !");
        } else {
            btnAjouter.setOnAction(event -> {
                System.out.println("✅ Clic détecté sur le bouton Ajouter !");
                ouvrirAjouterEquipement();
            });
        }

        try {
            EquipementService equipementService = new EquipementService();
            List<Equipement> equipements = equipementService.recuperer();

            ObservableList<Equipement> list = FXCollections.observableArrayList(equipements);
            listViewEquipements.setItems(list);

            listViewEquipements.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Equipement equipement, boolean empty) {
                    super.updateItem(equipement, empty);

                    if (empty || equipement == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Label nomLabel = new Label(equipement.getNom());
                        Label typeLabel = new Label(equipement.getType());
                        Label etatLabel = new Label(equipement.getEtat());
                        Label salleLabel = new Label("Salle " + equipement.getSalle_id());

                        nomLabel.setPrefWidth(150);
                        typeLabel.setPrefWidth(100);
                        etatLabel.setPrefWidth(100);
                        salleLabel.setPrefWidth(80);

                        nomLabel.setStyle("-fx-font-weight: bold;");
                        typeLabel.setStyle("-fx-text-fill: #444;");
                        etatLabel.setStyle("-fx-text-fill: #666;");
                        salleLabel.setStyle("-fx-text-fill: #888;");

                        // Boutons
                        Button btnModifier = new Button("Modifier");
                        Button btnSupprimer = new Button("Supprimer");

                        btnModifier.setStyle("-fx-background-color: #294478; -fx-background-radius: 20; -fx-text-fill: white;");
                        btnSupprimer.setStyle("-fx-background-color: #294478; -fx-background-radius: 20; -fx-text-fill: white;");

                        btnModifier.setOnAction(e -> modifierEquipement(equipement));
                        btnSupprimer.setOnAction(e -> supprimerEquipement(equipement));

                        HBox infoBox = new HBox(nomLabel, typeLabel, etatLabel, salleLabel);
                        infoBox.setSpacing(20);

                        HBox buttonBox = new HBox(btnModifier, btnSupprimer);
                        buttonBox.setSpacing(10);

                        HBox row = new HBox(infoBox, buttonBox);
                        row.setSpacing(30);
                        row.setStyle("-fx-padding: 10px; -fx-background-color: white; -fx-border-color: #ccc;");
                        row.setPrefWidth(750);

                        setGraphic(row);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAide() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ListeSalle.fxml"));

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Salle");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la page des salles");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void ouvrirAjouterEquipement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AjouterEquipement.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un équipement");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la page AjouterEquipement");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void modifierEquipement(Equipement equipement) {
        System.out.println("🔧 Modifier : " + equipement.getNom());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ModifierEquipement.fxml"));
            Parent root = loader.load();

            // Passer l'équipement sélectionné au contrôleur
            ModifierEquipement modifierController = loader.getController();

            modifierController.setEquipement(equipement);
            Stage stage = new Stage();
            stage.setTitle("Modifier un équipement");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la page ModifierEquipement");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void supprimerEquipement(Equipement equipement) {
        System.out.println("🗑️ Supprimer : " + equipement.getNom());

        // Boîte de confirmation
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet équipement ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        // Afficher la boîte de confirmation et attendre la réponse
        if (confirmationAlert.showAndWait().get() != ButtonType.OK) {
            return; // Si l'utilisateur clique sur "Annuler", on arrête la suppression
        }

        try {
            EquipementService equipementService = new EquipementService();
            equipementService.supprimer(equipement.getId());

            // Actualiser la ListView en supprimant l'élément de la liste
            listViewEquipements.getItems().remove(equipement);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Suppression impossible");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
