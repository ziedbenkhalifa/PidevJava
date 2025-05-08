package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.stage.Modality;
import tn.cinema.controllers.ModifierSalle;
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
import javafx.scene.image.ImageView;




public class ListeSalle {

    public Button btnGererEquipement;
    @FXML
    private ListView<Salle> listViewSalles;

    @FXML
    private Button btnAjouter;


    @FXML
    private TextField tfRecherche;



    @FXML
    private ImageView btnRecherche;

    @FXML
    private ComboBox<String> cbEtage;
    @FXML
    private Button btnTrier;

    @FXML
    public void initialize() {
        if (btnAjouter == null) {
            System.out.println("⚠️ btnAjouter n'est pas lié. Vérifiez le fx:id dans le FXML !");
        } else {
            btnAjouter.setOnAction(event -> {
                System.out.println("✅ Clic détecté sur le bouton Ajouter !");
                ouvrirAjouterSalle();
            });
            btnRecherche.setOnMouseClicked(event -> rechercherSalle());

            btnTrier.setOnAction(e -> trierParEtage());

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
        btnTrier.setOnAction(event -> trierParEtage());

    }



    @FXML
    private void rechercherSalle() {
        String nom = tfRecherche.getText().trim();
        SalleService service = new SalleService();

        try {
            List<Salle> resultats = service.rechercherParNom(nom);
            listViewSalles.getItems().setAll(resultats);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la recherche des salles.");
            alert.showAndWait();
        }
    }
    @FXML
    private void showNotificationList() {
        try {
            // Charger Notification.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Notification.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle fenêtre pour la liste des notifications
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Notifications");
            stage.initModality(Modality.APPLICATION_MODAL); // Fenêtre modale
            stage.setResizable(false); // Non redimensionnable
            stage.show();
        } catch (IOException e) {
            // Afficher une alerte en cas d'erreur
            showErrorAlert(e);
        }
    }

    private void showErrorAlert(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de chargement");
        alert.setHeaderText("Impossible de charger la liste des notifications");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void trierParEtage() {
        String etageChoisi = cbEtage.getValue(); // Récupère l'étage sélectionné dans le ComboBox
        if (etageChoisi != null && !etageChoisi.isEmpty()) {
            SalleService service = new SalleService();
            try {
                // Récupère les salles filtrées par l'étage choisi
                List<Salle> resultats = service.getSallesParEtage(etageChoisi);
                ObservableList<Salle> list = FXCollections.observableArrayList(resultats);
                listViewSalles.setItems(list); // Met à jour la ListView avec les salles filtrées
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors du tri des salles");
                alert.setContentText("Une erreur s'est produite lors de la récupération des salles.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alerte");
            alert.setHeaderText("Sélectionnez un étage");
            alert.setContentText("Veuillez sélectionner un étage avant de trier.");
            alert.showAndWait();
        }
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