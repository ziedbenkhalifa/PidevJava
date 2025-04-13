package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Publicite;
import tn.cinema.services.PubliciteService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;

public class PubliciteClient implements Initializable {
    @FXML
    private Button publicitesButton;

    @FXML
    private Button demandeSubButton;

    @FXML
    private Button publiciteSubButton;

    @FXML
    private Button backButton;

    @FXML
    private ListView<Publicite> publiciteListView;

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
                    VBox vbox = new VBox(10);
                    vbox.setStyle("-fx-background-color: " + (getIndex() % 2 == 0 ? "#f0f4f8" : "#e8ecef") + ";" +
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #064625;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");

                    // Use GridPane to organize Publicite details
                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(10);
                    gridPane.setVgap(5);

                    // Define fields to display
                    Text idText = new Text("ID: " + publicite.getId());
                    idText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text demandeIdText = new Text("Demande ID: " + publicite.getDemandeId());
                    demandeIdText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text dateDebutText = new Text("Date Début: " + publicite.getDateDebut());
                    dateDebutText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text dateFinText = new Text("Date Fin: " + publicite.getDateFin());
                    dateFinText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text supportText = new Text("Support: " + publicite.getSupport());
                    supportText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                    Text montantText = new Text("Montant: " + publicite.getMontant());
                    montantText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                    // Add fields to GridPane
                    gridPane.add(idText, 0, 0);
                    gridPane.add(demandeIdText, 1, 0);
                    gridPane.add(dateDebutText, 0, 1);
                    gridPane.add(dateFinText, 1, 1);
                    gridPane.add(supportText, 0, 2);
                    gridPane.add(montantText, 1, 2);

                    // Add GridPane to the VBox (no buttons since CRUD is not allowed)
                    vbox.getChildren().add(gridPane);

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
        loadPublicites();
    }

    private void loadPublicites() {
        try {
            publicites = FXCollections.observableArrayList(publiciteService.recuperePublicitesParClient());
            publiciteListView.setItems(publicites);
            System.out.println("Nombre de publicités récupérées : " + publicites.size());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des publicités : " + e.getMessage());
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemandeClient.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToPubliciteClient(ActionEvent event) throws IOException {
        // Already on PubliciteClient, no action needed
    }

    @FXML
    public void goBackToFront(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Front.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Client Dashboard");
            stage.show();
            System.out.println("Navigated back to Front.fxml from PubliciteClient");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Front.fxml: " + e.getMessage());
        }
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