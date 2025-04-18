package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import tn.cinema.services.PubliciteService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class InterfaceDemandes implements Initializable {
    @FXML
    private ListView<Demande> demandeListView;

    @FXML
    private Button ajouterDemandeButton;

    @FXML
    private Button backButton;

    private DemandeService demandeService = new DemandeService();
    private PubliciteService publiciteService; // Moved to class level

    private ObservableList<Demande> demandes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize publiciteService
        try {
            publiciteService = new PubliciteService();
            System.out.println("PubliciteService initialized successfully: " + (publiciteService != null));
        } catch (Exception e) {
            System.err.println("Failed to initialize PubliciteService: " + e.getMessage());
            e.printStackTrace();
        }

        // Set up the ListView with a custom cell factory
        demandeListView.setCellFactory(listView -> new ListCell<Demande>() {
            @Override
            protected void updateItem(Demande demande, boolean empty) {
                super.updateItem(demande, empty);
                if (empty || demande == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(""); // Reset style for empty cells
                } else {
                    // Create a custom layout for each Demande item
                    VBox vbox = new VBox(10);
                    vbox.setStyle("-fx-background-color: " + (getIndex() % 2 == 0 ? "#f0f4f8" : "#e8ecef") + ";" +
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #064625;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");

                    // Use GridPane to organize Demande details in a structured layout
                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(10);
                    gridPane.setVgap(5);

                    // Define fields to display
                    Text idText = new Text("ID: " + demande.getId());
                    idText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text userIdText = new Text("User ID: " + demande.getUserId());
                    userIdText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                 //  Text adminIdText = new Text("Admin ID: " + (demande.getAdminId() != null ? demande.getAdminId() : "N/A"));
                   // adminIdText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text nbJoursText = new Text("Nb Jours: " + demande.getNombreJours());
                    nbJoursText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text descriptionText = new Text("Description: " + demande.getDescription());
                    descriptionText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    descriptionText.setWrappingWidth(300); // Wrap long description
                    Text typeText = new Text("Type: " + demande.getType());
                    typeText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text lienSuppText = new Text("Lien Supp: "
                            + (demande.getLienSupplementaire() != null ? demande.getLienSupplementaire() : "N/A"));
                    lienSuppText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    lienSuppText.setWrappingWidth(300); // Wrap long lienSupplementaire
                    Text statutText = new Text("Statut: " + transformStatutForUI(demande.getStatut()));
                    statutText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text dateText = new Text("Date: " + demande.getDateSoumission());
                    dateText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                    // Add fields to GridPane
                    gridPane.add(idText, 0, 0);
                    gridPane.add(userIdText, 1, 0);
                //    gridPane.add(adminIdText, 2, 0);
                    gridPane.add(nbJoursText, 0, 1);
                    gridPane.add(descriptionText, 1, 1, 2, 1); // Span 2 columns
                    gridPane.add(typeText, 0, 2);
                    gridPane.add(lienSuppText, 1, 2, 2, 1); // Span 2 columns
                    gridPane.add(statutText, 0, 3);
                    gridPane.add(dateText, 1, 3);

                    // Create buttons for Modifier, Supprimer, and conditionally Ajouter Publicité
                    HBox buttonsBox = new HBox(10);
                    buttonsBox.setStyle("-fx-padding: 10 0 0 0; -fx-alignment: center-right;"); // Align buttons to the right

                    Button modifierButton = new Button("Modifier");
                    modifierButton.setStyle("-fx-background-color: #294478; -fx-text-fill: #d7d7d9; -fx-background-radius: 5; -fx-font-size: 14;");
                    modifierButton.setOnMouseEntered(e -> modifierButton.setStyle("-fx-background-color: #3b5a9a; -fx-text-fill: #d7d7d9; -fx-background-radius: 5; -fx-font-size: 14;"));
                    modifierButton.setOnMouseExited(e -> modifierButton.setStyle("-fx-background-color: #294478; -fx-text-fill: #d7d7d9; -fx-background-radius: 5; -fx-font-size: 14;"));
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
                    supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: #ffffff; -fx-background-radius: 5; -fx-font-size: 14;");
                    supprimerButton.setOnMouseEntered(e -> supprimerButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: #ffffff; -fx-background-radius: 5; -fx-font-size: 14;"));
                    supprimerButton.setOnMouseExited(e -> supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: #ffffff; -fx-background-radius: 5; -fx-font-size: 14;"));
                    supprimerButton.setOnAction(event -> {
                        try {
                            openSupprimerDemande(demande);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de suppression : " + e.getMessage());
                            alert.showAndWait();
                        }
                    });

                    // Check if the Demande is approuvee and has no associated Publicite
                    boolean isApprouvee = "approuvee".equalsIgnoreCase(demande.getStatut().trim());
                    boolean hasPublicite = false;
                    try {
                        if (publiciteService == null) {
                            System.err.println("publiciteService is null for Demande ID: " + demande.getId());
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur : Service de publicité non initialisé.");
                            alert.showAndWait();
                        } else {
                            hasPublicite = publiciteService.existsByDemandeId(demande.getId());
                            System.out.println("Demande ID: " + demande.getId() +
                                    ", Raw Statut: \"" + demande.getStatut() + "\"" +
                                    ", Trimmed Statut: \"" + demande.getStatut().trim() + "\"" +
                                    ", Is Approuvee: " + isApprouvee +
                                    ", Has Publicite: " + hasPublicite);
                        }
                    } catch (SQLException e) {
                        System.err.println("SQLException while checking Publicite for Demande ID: " + demande.getId());
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la vérification de la publicité : " + e.getMessage());
                        alert.showAndWait();
                    } catch (Exception e) {
                        System.err.println("Unexpected error while checking Publicite for Demande ID: " + demande.getId());
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur inattendue : " + e.getMessage());
                        alert.showAndWait();
                    }

                    if (isApprouvee && !hasPublicite) {
                        System.out.println("Showing Ajouter Publicité button for Demande ID: " + demande.getId());
                        Button ajouterPubliciteButton = new Button("Ajouter Publicité");
                        ajouterPubliciteButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #ffffff; -fx-background-radius: 5; -fx-font-size: 14; -fx-padding: 5 15;");
                        ajouterPubliciteButton.setOnMouseEntered(e -> ajouterPubliciteButton.setStyle("-fx-background-color: #66BB6A; -fx-text-fill: #ffffff; -fx-background-radius: 5; -fx-font-size: 14; -fx-padding: 5 15;"));
                        ajouterPubliciteButton.setOnMouseExited(e -> ajouterPubliciteButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #ffffff; -fx-background-radius: 5; -fx-font-size: 14; -fx-padding: 5 15;"));
                        ajouterPubliciteButton.setOnAction(event -> {
                            try {
                                openAjouterPubliciteFromDemande(demande);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre Ajouter Publicité : " + e.getMessage());
                                alert.showAndWait();
                            }
                        });
                        buttonsBox.getChildren().addAll(modifierButton, supprimerButton, ajouterPubliciteButton);
                    } else {
                        System.out.println("Not showing Ajouter Publicité button for Demande ID: " + demande.getId() +
                                " (Is Approuvee: " + isApprouvee + ", Has Publicite: " + hasPublicite + ")");
                        buttonsBox.getChildren().addAll(modifierButton, supprimerButton);
                    }

                    // Add GridPane and buttons to the VBox
                    vbox.getChildren().addAll(gridPane, buttonsBox);

                    // Add hover effect for the entire cell
                    vbox.setOnMouseEntered(e -> vbox.setStyle("-fx-background-color: " + (getIndex() % 2 == 0 ? "#e6ecf2" : "#dfe4e8") + ";" +
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #064625;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.7, 3, 3);"));
                    vbox.setOnMouseExited(e -> vbox.setStyle("-fx-background-color: " + (getIndex() % 2 == 0 ? "#f0f4f8" : "#e8ecef") + ";" +
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #064625;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);"));

                    setGraphic(vbox);
                }
            }
        });

        // Load data
        loadDemandes();
    }

    private void loadDemandes() {
        try {
            demandes = FXCollections.observableArrayList(demandeService.recuperer());
            for (Demande demande : demandes) {
                System.out.println("Loaded Demande - ID: " + demande.getId() + ", Statut: \"" + demande.getStatut() + "\"");
            }
            demandeListView.setItems(demandes);
            System.out.println("Nombre de demandes récupérées : " + demandes.size());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des demandes : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouterDemandeClick() throws Exception {
        // Load AjouterDemande.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterDemande.fxml"));
        Parent root = loader.load();

        // Get the controller for AjouterDemande
        AjouterDemande controller = loader.getController();
        controller.setParentController(this);

        // Create a new stage for the AjouterDemande window
        Stage stage = new Stage();
        stage.setTitle("Ajouter une Demande");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openModifierDemande(Demande demande) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDemande.fxml"));
        Parent root = loader.load();

        ModifierDemande controller = loader.getController();
        controller.setDemande(demande);
        controller.setParentController(this);

        Stage stage = new Stage();
        stage.setTitle("Modifier la Demande");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openSupprimerDemande(Demande demande) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SupprimerDemande.fxml"));
        Parent root = loader.load();

        SupprimerDemande controller = loader.getController();
        controller.setDemande(demande);
        controller.setParentController(this);

        Stage stage = new Stage();
        stage.setTitle("Supprimer la Demande");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openAjouterPubliciteFromDemande(Demande demande) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPublicite.fxml"));
        Parent root = loader.load();

        AjouterPublicite controller = loader.getController();
        controller.setParentController(this);
        controller.setDemandeId(demande.getId());

        Stage stage = new Stage();
        stage.setTitle("Ajouter une Publicité");
        stage.setScene(new Scene(root));
        stage.show();
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
    private void goBackToInterfaceChoixGP() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceChoixGP.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion Publicités");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading InterfaceChoixGP.fxml: " + e.getMessage());
        }
    }
    @FXML
    private Button backButtonn;
    public void goBackToLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButtonn.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Dashboard.fxml: " + e.getMessage());
        }
    }
}