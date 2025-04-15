package tn.cinema.Controllers;

import javafx.event.ActionEvent;
import tn.cinema.entities.Salle;
import tn.cinema.services.SalleService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import tn.cinema.entities.Salle;

public class ListeSalle {

    public Button btnGererEquipement;
    @FXML
    private ListView<Salle> listViewSalles;

    @FXML
    private Button btnAjouter;
    

    @FXML
    public void initialize() {
        if (btnAjouter == null) {
            System.out.println("⚠️ btnAjouter n'est pas lié. Vérifiez le fx:id dans le FXML !");
        } else {
            btnAjouter.setOnAction(event -> {
                System.out.println("✅ Clic détecté sur le bouton Ajouter !");
                ouvrirAjouterSalle();
            });
        }

        try {
            SalleService salleService = new SalleService();
            List<Salle> salles = salleService.recuperer();

            ObservableList<Salle> list = FXCollections.observableArrayList(salles);
            listViewSalles.setItems(list);

            listViewSalles.setCellFactory(param -> new ListCell<Salle>() {
                @Override
                protected void updateItem(Salle salle, boolean empty) {
                    super.updateItem(salle, empty);

                    if (empty || salle == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Création des labels
                        Label nomLabel = new Label("Nom: " + salle.getNom_salle());
                        Label dispoLabel = new Label("Disponibilité: " + salle.getDisponibilite());
                        Label typeLabel = new Label("Type: " + salle.getType_salle());
                        Label statutLabel = new Label("Statut: " + salle.getStatut());
                        Label emplacementLabel = new Label("Emplacement: " + salle.getEmplacement());

                        Label placesLabel = new Label("Places: " + salle.getNombre_de_place());

                        // Styles et tailles
                        nomLabel.setPrefWidth(150);
                        dispoLabel.setPrefWidth(120);
                        typeLabel.setPrefWidth(100);
                        placesLabel.setPrefWidth(100);
                        statutLabel.setPrefWidth(100);
                        emplacementLabel.setPrefWidth(150);

                        nomLabel.setStyle("-fx-font-weight: bold;");
                        dispoLabel.setStyle("-fx-text-fill: #444;");
                        typeLabel.setStyle("-fx-text-fill: #666;");
                        placesLabel.setStyle("-fx-text-fill: #333;");
                        statutLabel.setStyle("-fx-text-fill: #555;");
                        emplacementLabel.setStyle("-fx-text-fill: #555;");

                        // Boutons
                        Button btnModifier = new Button("Modifier");
                        Button btnSupprimer = new Button("Supprimer");

                        btnModifier.setStyle("-fx-background-color: #294478; -fx-background-radius: 20; -fx-text-fill: white;");
                        btnSupprimer.setStyle("-fx-background-color: #294478; -fx-background-radius: 20; -fx-text-fill: white;");

                        btnSupprimer.setOnAction(e -> supprimerSalle(salle));
                        btnModifier.setOnAction(e -> ModifierSalle(salle));


                        // Box infos
                        HBox infoBox = new HBox(nomLabel, dispoLabel, typeLabel, placesLabel, statutLabel, emplacementLabel);
                        infoBox.setSpacing(20);

                        // Box boutons
                        HBox buttonBox = new HBox(btnModifier, btnSupprimer);
                        buttonBox.setSpacing(10);

                        // Ligne complète
                        HBox row = new HBox(infoBox, buttonBox);
                        row.setSpacing(30);
                        row.setStyle("-fx-padding: 10px; -fx-background-color: white; -fx-border-color: #ccc;");
                        row.setPrefWidth(900);

                        setGraphic(row);
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }



        btnGererEquipement.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ListeEquipement.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnGererEquipement.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }





    // Ouvrir la page AjouterSalle pour ajouter une nouvelle salle
    private void ouvrirAjouterSalle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AjouterSalle.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une salle");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la page AjouterSalle");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // Supprimer une salle
    private void supprimerSalle(Salle salle) {
        System.out.println("🗑️ Supprimer : " + salle.getNom_salle());

        // Boîte de confirmation
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette salle ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        // Afficher la boîte de confirmation et attendre la réponse
        if (confirmationAlert.showAndWait().get() != ButtonType.OK) {
            return; // Si l'utilisateur clique sur "Annuler", on arrête la suppression
        }

        try {
            SalleService salleService = new SalleService();
            salleService.supprimer(salle.getId_salle());

            // Actualiser la ListView en supprimant l'élément de la liste
            listViewSalles.getItems().remove(salle);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Suppression impossible");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // Méthode pour ouvrir la fenêtre de modification de la salle
    private void ModifierSalle(Salle salle) {
        System.out.println("🔧 Modifier la salle : " + salle.getNom_salle());

        try {
            // Charger la fenêtre de modification de salle (FXML)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ModifierSalle.fxml"));
            Parent root = loader.load();

            // Passer la salle sélectionnée au contrôleur de la fenêtre ModifierSalle
            ModifierSalle modifierController = loader.getController();
            modifierController.setSalle(salle);  // Passer la salle à la fenêtre de modification

            // Ouvrir la fenêtre de modification
            Stage stage = new Stage();
            stage.setTitle("Modifier une salle");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la page ModifierSalle");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }



}
