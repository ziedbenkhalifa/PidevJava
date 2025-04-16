package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DemandeClient extends FrontzController implements Initializable {
    @FXML
    private Button publicitesButton;

    @FXML
    private Button demandeSubButton;

    @FXML
    private Button publiciteSubButton;

    @FXML
    private ListView<Demande> demandeListView;

    @FXML
    private Button backButton;

    @FXML
    private Button ajouterDemandeButton;

    private DemandeService demandeService = new DemandeService();
    private ObservableList<Demande> demandes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the ListView with a custom cell factory
        demandeListView.setCellFactory(listView -> new ListCell<Demande>() {
            @Override
            protected void updateItem(Demande demande, boolean empty) {
                super.updateItem(demande, empty);
                if (empty || demande == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    // Create a custom layout for each Demande item
                    VBox vbox = new VBox(10);
                    vbox.setStyle("-fx-background-color: linear-gradient(to right, #1a2a44, #2a3b5a);" +
                            "-fx-padding: 20;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #4a5e7a;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0.3, 3, 3);");

                    // Use GridPane to organize Demande details
                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(15);
                    gridPane.setVgap(8);

                    // Define fields to display (excluding userId and adminId)
                    Text idText = new Text("ID: " + demande.getId());
                    idText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #ffffff;");
                    Text nbJoursText = new Text("Nb Jours: " + demande.getNombreJours());
                    nbJoursText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #ffffff;");
                    Text descriptionText = new Text("Description: " + demande.getDescription());
                    descriptionText.setStyle("-fx-font-size: 14; -fx-fill: #d7d7d9;");
                    descriptionText.setWrappingWidth(350);
                    Text typeText = new Text("Type: " + demande.getType());
                    typeText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #ffffff;");
                    Text lienSuppText = new Text("Lien Supp: " + (demande.getLienSupplementaire() != null ? demande.getLienSupplementaire() : "N/A"));
                    lienSuppText.setStyle("-fx-font-size: 14; -fx-fill: #d7d7d9;");
                    lienSuppText.setWrappingWidth(350);
                    Text statutText = new Text("Statut: " + transformStatutForUI(demande.getStatut()));
                    statutText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #ffffff;");
                    Text dateText = new Text("Date: " + demande.getDateSoumission());
                    dateText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #ffffff;");

                    // Add fields to GridPane (excluding userId and adminId)
                    gridPane.add(idText, 0, 0);
                    gridPane.add(nbJoursText, 1, 0);
                    gridPane.add(descriptionText, 0, 1, 2, 1); // Span 2 columns
                    gridPane.add(typeText, 0, 2);
                    gridPane.add(lienSuppText, 1, 2);
                    gridPane.add(statutText, 0, 3);
                    gridPane.add(dateText, 1, 3);

                    // Create buttons for Modifier and Supprimer
                    HBox buttonsBox = new HBox(15);
                    buttonsBox.setStyle("-fx-padding: 15 0 0 0; -fx-alignment: center-right;");

                    Button modifierButton = new Button("Modifier");
                    modifierButton.setStyle("-fx-background-color: #3b5a9a; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 8 20;");
                    modifierButton.setOnMouseEntered(e -> modifierButton.setStyle("-fx-background-color: #4a6cbb; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 8 20;"));
                    modifierButton.setOnMouseExited(e -> modifierButton.setStyle("-fx-background-color: #3b5a9a; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 8 20;"));
                    modifierButton.setOnAction(event -> {
                        try {
                            openModifierDemande(demande);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
                            alert.showAndWait();
                        }
                    });

                    Button supprimerButton = new Button("Supprimer");
                    supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 8 20;");
                    supprimerButton.setOnMouseEntered(e -> supprimerButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 8 20;"));
                    supprimerButton.setOnMouseExited(e -> supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 8 20;"));
                    supprimerButton.setOnAction(event -> {
                        try {
                            openSupprimerDemande(demande);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de suppression : " + e.getMessage());
                            alert.showAndWait();
                        }
                    });

                    buttonsBox.getChildren().addAll(modifierButton, supprimerButton);

                    // Add GridPane and buttons to the VBox
                    vbox.getChildren().addAll(gridPane, buttonsBox);

                    // Add hover effect for the entire cell
                    vbox.setOnMouseEntered(e -> vbox.setStyle("-fx-background-color: linear-gradient(to right, #2a3b5a, #3b4c7a);" +
                            "-fx-padding: 20;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #5a6e9a;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15, 0.5, 5, 5);"));
                    vbox.setOnMouseExited(e -> vbox.setStyle("-fx-background-color: linear-gradient(to right, #1a2a44, #2a3b5a);" +
                            "-fx-padding: 20;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #4a5e7a;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0.3, 3, 3);"));

                    setGraphic(vbox);
                }
            }
        });

        // Load data
        loadDemandes();
    }

    private void loadDemandes() {
        try {
            demandes = FXCollections.observableArrayList(demandeService.recupereDemandesParClient());
            demandeListView.setItems(demandes);
            System.out.println("Nombre de demandes récupérées : " + demandes.size());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des demandes : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouterDemandeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterDemandeClient.fxml"));
            Parent root = loader.load();

            AjouterDemandeClientController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Demande");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre d'ajout : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void openModifierDemande(Demande demande) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDemandeClient.fxml"));
        Parent root = loader.load();

        ModifierDemandeClientController controller = loader.getController();
        controller.setDemande(demande);
        controller.setParentController(this);

        Stage stage = new Stage();
        stage.setTitle("Modifier la Demande");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openSupprimerDemande(Demande demande) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SupprimerDemandeClient.fxml"));
        Parent root = loader.load();

        SupprimerDemandeClientController controller = loader.getController();
        controller.setDemande(demande);
        controller.setParentController(this);

        Stage stage = new Stage();
        stage.setTitle("Supprimer la Demande");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void goBackToFront(ActionEvent event) {
        try {
            // Adjust the path based on the actual location of Front.fxml
            String fxmlPath = "/FrontZ.fxml"; // Update this if Front.fxml is in a different package, e.g., "/tn/cinema/views/Front.fxml"
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find resource: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Client Dashboard");
            stage.show();
            System.out.println("Navigated back to Front.fxml from DemandeClient");
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du retour au tableau de bord : " + e.getMessage());
            alert.showAndWait();
            System.err.println("Error loading Front.fxml: " + e.getMessage());
        }
    }

    public void refreshList() {
        loadDemandes();
    }

    private String transformStatutForUI(String statut) {
        if (statut == null) return "Inconnu";
        switch (statut.toLowerCase()) {
            case "approuvee":
                return "Approuvée";
            case "en_attente":
                return "En attente";
            case "rejete":
                return "Rejetée";
            default:
                return statut;
        }
    }

    @FXML
    public void toggleSubButtons(ActionEvent event) {
        boolean isVisible = demandeSubButton.isVisible();
        demandeSubButton.setVisible(!isVisible);
        publiciteSubButton.setVisible(!isVisible);
    }

    @FXML
    public void goToDemandeClient(ActionEvent event) throws IOException {
        // Already on DemandeClient, no action needed
    }

    @FXML
    public void goToPubliciteClient(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PubliciteClient.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
        System.out.println("Logged out and navigated to Login.fxml");
    }
}