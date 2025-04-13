package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Publicite;
import tn.cinema.services.PubliciteService;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class InterfacePublicites implements Initializable {
    @FXML
    private ListView<Publicite> publiciteListView;

    @FXML
    private Button ajouterPubliciteButton;

    @FXML
    private Button backButton;

    private PubliciteService publiciteService = new PubliciteService();

    private ObservableList<Publicite> publicites;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the ListView with a custom cell factory
        publiciteListView.setCellFactory(listView -> new ListCell<Publicite>() {
            @Override
            protected void updateItem(Publicite publicite, boolean empty) {
                super.updateItem(publicite, empty);
                if (empty || publicite == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    // Create a custom layout for each Publicite item
                    VBox card = new VBox(15);
                    card.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #f5f7fa);" +
                            "-fx-padding: 20;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #b0bec5;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0.5, 3, 3);");

                    // Use GridPane to organize Publicite details
                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(15);
                    gridPane.setVgap(10);

                    // Define fields with styled labels and values
                    // ID (emphasized)
                    Text idLabel = new Text("ID: ");
                    idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-fill: #37474f;");
                    Text idValue = new Text(String.valueOf(publicite.getId()));
                    idValue.setStyle("-fx-font-size: 16; -fx-fill: #546e7a;");

                    // Demande ID
                    Text demandeIdLabel = new Text("Demande ID: ");
                    demandeIdLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-fill: #37474f;");
                    Text demandeIdValue = new Text(String.valueOf(publicite.getDemandeId()));
                    demandeIdValue.setStyle("-fx-font-size: 14; -fx-fill: #546e7a;");

                    // Date Debut (formatted)
                    Text dateDebutLabel = new Text("Date Début: ");
                    dateDebutLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-fill: #37474f;");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDateDebut = dateFormat.format(publicite.getDateDebut());
                    Text dateDebutValue = new Text(formattedDateDebut);
                    dateDebutValue.setStyle("-fx-font-size: 14; -fx-fill: #546e7a;");

                    // Date Fin (formatted)
                    Text dateFinLabel = new Text("Date Fin: ");
                    dateFinLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-fill: #37474f;");
                    String formattedDateFin = dateFormat.format(publicite.getDateFin());
                    Text dateFinValue = new Text(formattedDateFin);
                    dateFinValue.setStyle("-fx-font-size: 14; -fx-fill: #546e7a;");

                    // Support
                    Text supportLabel = new Text("Support: ");
                    supportLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-fill: #37474f;");
                    Text supportValue = new Text(publicite.getSupport());
                    supportValue.setStyle("-fx-font-size: 14; -fx-fill: #546e7a;");

                    // Montant
                    Text montantLabel = new Text("Montant: ");
                    montantLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-fill: #37474f;");
                    Text montantValue = new Text(String.format("%.2f", publicite.getMontant()));
                    montantValue.setStyle("-fx-font-size: 14; -fx-fill: #546e7a;");

                    // Add fields to GridPane
                    gridPane.add(idLabel, 0, 0);
                    gridPane.add(idValue, 1, 0);
                    gridPane.add(demandeIdLabel, 2, 0);
                    gridPane.add(demandeIdValue, 3, 0);
                    gridPane.add(dateDebutLabel, 0, 1);
                    gridPane.add(dateDebutValue, 1, 1);
                    gridPane.add(dateFinLabel, 2, 1);
                    gridPane.add(dateFinValue, 3, 1);
                    gridPane.add(supportLabel, 0, 2);
                    gridPane.add(supportValue, 1, 2);
                    gridPane.add(montantLabel, 2, 2);
                    gridPane.add(montantValue, 3, 2);

                    // Create buttons
                    HBox buttonsBox = new HBox(10);
                    buttonsBox.setStyle("-fx-padding: 15 0 0 0; -fx-alignment: center-right;");

                    // Modifier Button
                    Button modifierButton = new Button("Modifier");
                    modifierButton.setStyle("-fx-background-color: #294478; -fx-text-fill: #d7d7d9; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                    modifierButton.setOnMouseEntered(e -> {
                        modifierButton.setStyle("-fx-background-color: #3b5a9a; -fx-text-fill: #d7d7d9; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                        modifierButton.setScaleX(1.05);
                        modifierButton.setScaleY(1.05);
                    });
                    modifierButton.setOnMouseExited(e -> {
                        modifierButton.setStyle("-fx-background-color: #294478; -fx-text-fill: #d7d7d9; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                        modifierButton.setScaleX(1.0);
                        modifierButton.setScaleY(1.0);
                    });
                    modifierButton.setOnAction(event -> {
                        try {
                            openModifierPublicite(publicite);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
                            alert.showAndWait();
                        }
                    });

                    // Supprimer Button
                    Button supprimerButton = new Button("Supprimer");
                    supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                    supprimerButton.setOnMouseEntered(e -> {
                        supprimerButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                        supprimerButton.setScaleX(1.05);
                        supprimerButton.setScaleY(1.05);
                    });
                    supprimerButton.setOnMouseExited(e -> {
                        supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                        supprimerButton.setScaleX(1.0);
                        supprimerButton.setScaleY(1.0);
                    });
                    supprimerButton.setOnAction(event -> {
                        try {
                            openSupprimerPublicite(publicite);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de suppression : " + e.getMessage());
                            alert.showAndWait();
                        }
                    });

                    buttonsBox.getChildren().addAll(modifierButton, supprimerButton);

                    // Add a separator line below the card
                    Rectangle separator = new Rectangle();
                    separator.setHeight(1);
                    separator.setFill(Color.web("#b0bec5"));
                    separator.setStyle("-fx-opacity: 0.5;");

                    // Add GridPane and buttons to the card
                    card.getChildren().addAll(gridPane, buttonsBox, separator);

                    // Add hover effect for the entire card
                    card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: linear-gradient(to bottom, #fafafa, #eceff1);" +
                            "-fx-padding: 20;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #78909c;" +
                            "-fx-border-width: 2;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0.7, 4, 4);"));
                    card.setOnMouseExited(e -> card.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #f5f7fa);" +
                            "-fx-padding: 20;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #b0bec5;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0.5, 3, 3);"));

                    setGraphic(card);
                }
            }
        });

        // Load data
        loadPublicites();
    }

    private void loadPublicites() {
        try {
            publicites = FXCollections.observableArrayList(publiciteService.recupererpub());
            publiciteListView.setItems(publicites);
            System.out.println("Nombre de publicités récupérées : " + publicites.size());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des publicités : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouterPubliciteClick() throws Exception {
        // Load AjouterPublicite.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPublicite.fxml"));
        Parent root = loader.load();

        // Get the controller for AjouterPublicite
        AjouterPublicite controller = loader.getController();
        controller.setParentController(this);

        // Create a new stage for the AjouterPublicite window
        Stage stage = new Stage();
        stage.setTitle("Ajouter une Publicité");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openModifierPublicite(Publicite publicite) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierPublicite.fxml"));
        Parent root = loader.load();

        ModifierPublicite controller = loader.getController();
        controller.setPublicite(publicite);
        controller.setParentController(this);

        Stage stage = new Stage();
        stage.setTitle("Modifier la Publicité");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openSupprimerPublicite(Publicite publicite) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SupprimerPublicite.fxml"));
        Parent root = loader.load();

        SupprimerPublicite controller = loader.getController();
        controller.setPublicite(publicite);
        controller.setParentController(this);

        Stage stage = new Stage();
        stage.setTitle("Supprimer la Publicité");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Method to refresh the ListView after adding, modifying, or deleting a Publicite
    public void refreshList() {
        loadPublicites();
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
}