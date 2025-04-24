package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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

public class InterfaceDemandes extends Dashboard implements Initializable {
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
                    setStyle("");
                } else {
                    VBox vbox = new VBox(10);
                    vbox.setStyle("-fx-background-color: #021b50;" + // Couleur de fond bleu foncé
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #064625;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");

                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(10);
                    gridPane.setVgap(5);

                    Text idText = new Text("ID: " + demande.getId());
                    Text userIdText = new Text("User ID: " + demande.getUserId());
                    Text nbJoursText = new Text("Nb Jours: " + demande.getNombreJours());
                    Text descriptionText = new Text("Description: " + demande.getDescription());
                    descriptionText.setWrappingWidth(300);
                    Text typeText = new Text("Type: " + demande.getType());
                    Text lienSuppText = new Text("Lien Supp: " + (demande.getLienSupplementaire() != null ? demande.getLienSupplementaire() : "N/A"));
                    lienSuppText.setWrappingWidth(300);
                    Text statutText = new Text("Statut: " + transformStatutForUI(demande.getStatut()));
                    Text dateText = new Text("Date: " + demande.getDateSoumission());

                    // Texte en blanc
                    for (Text text : new Text[]{idText, userIdText, nbJoursText, descriptionText, typeText, lienSuppText, statutText, dateText}) {
                        text.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
                    }

                    gridPane.add(idText, 0, 0);
                    gridPane.add(userIdText, 1, 0);
                    gridPane.add(nbJoursText, 0, 1);
                    gridPane.add(descriptionText, 1, 1, 2, 1);
                    gridPane.add(typeText, 0, 2);
                    gridPane.add(lienSuppText, 1, 2, 2, 1);
                    gridPane.add(statutText, 0, 3);
                    gridPane.add(dateText, 1, 3);

                    HBox buttonsBox = new HBox(10);
                    buttonsBox.setStyle("-fx-padding: 10 0 0 0; -fx-alignment: center-right;");

                    // MODIFIER (vert)
                    Button modifierButton = new Button("Modifier");
                    modifierButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;");
                    modifierButton.setOnMouseEntered(e -> modifierButton.setStyle("-fx-background-color: #34c759; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;"));
                    modifierButton.setOnMouseExited(e -> modifierButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;"));
                    modifierButton.setOnAction(event -> {
                        try {
                            openModifierDemande(demande);
                        } catch (Exception e) {
                            e.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage()).showAndWait();
                        }
                    });

                    // SUPPRIMER
                    Button supprimerButton = new Button("Supprimer");
                    supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;");
                    supprimerButton.setOnMouseEntered(e -> supprimerButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;"));
                    supprimerButton.setOnMouseExited(e -> supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;"));
                    supprimerButton.setOnAction(event -> {
                        try {
                            openSupprimerDemande(demande);
                        } catch (Exception e) {
                            e.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de suppression : " + e.getMessage()).showAndWait();
                        }
                    });

                    boolean isApprouvee = "approuvee".equalsIgnoreCase(demande.getStatut().trim());
                    boolean hasPublicite = false;
                    try {
                        hasPublicite = publiciteService != null && publiciteService.existsByDemandeId(demande.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isApprouvee && !hasPublicite) {
                        Button ajouterPubliciteButton = new Button("Ajouter Publicité");
                        ajouterPubliciteButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14; -fx-padding: 5 15;");
                        ajouterPubliciteButton.setOnMouseEntered(e -> ajouterPubliciteButton.setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14; -fx-padding: 5 15;"));
                        ajouterPubliciteButton.setOnMouseExited(e -> ajouterPubliciteButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14; -fx-padding: 5 15;"));
                        ajouterPubliciteButton.setOnAction(event -> {
                            try {
                                openAjouterPubliciteFromDemande(demande);
                            } catch (Exception e) {
                                e.printStackTrace();
                                new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre Ajouter Publicité : " + e.getMessage()).showAndWait();
                            }
                        });
                        buttonsBox.getChildren().addAll(modifierButton, supprimerButton, ajouterPubliciteButton);
                    } else {
                        buttonsBox.getChildren().addAll(modifierButton, supprimerButton);
                    }

                    vbox.getChildren().addAll(gridPane, buttonsBox);

                    // Hover effects sans changer la couleur de fond initiale
                    vbox.setOnMouseEntered(e -> vbox.setStyle("-fx-background-color: #032264;" +
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #064625;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.7, 3, 3);"));
                    vbox.setOnMouseExited(e -> vbox.setStyle("-fx-background-color: #021b50;" +
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