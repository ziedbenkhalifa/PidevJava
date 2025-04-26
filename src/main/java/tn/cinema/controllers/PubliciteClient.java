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
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Publicite;
import tn.cinema.services.PubliciteService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PubliciteClient extends FrontzController implements Initializable {
    @FXML
    private Button publicitesButton;

    @FXML
    private Button demandeSubButton;

    @FXML
    private Button publiciteSubButton;

    @FXML
    private Button backButton;

    @FXML
    private Button ajouterDemandeButton;

    @FXML
    private ListView<Publicite> publiciteListView;

    private PubliciteService publiciteService = new PubliciteService();
    private ObservableList<Publicite> publicites;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        publiciteListView.setCellFactory(listView -> new ListCell<Publicite>() {
            @Override
            protected void updateItem(Publicite publicite, boolean empty) {
                super.updateItem(publicite, empty);
                if (empty || publicite == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    VBox vbox = new VBox(10);
                    vbox.setStyle("-fx-background-color: linear-gradient(to right, #1a2a44, #2a3b5a);" +
                            "-fx-padding: 20;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #4a5e7a;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0.3, 3, 3);");

                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(15);
                    gridPane.setVgap(8);

                    Text demandeIdText = new Text("Demande ID: " + publicite.getDemandeId());
                    demandeIdText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #ffffff;");
                    Text dateDebutText = new Text("Date Début: " + publicite.getDateDebut());
                    dateDebutText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #ffffff;");
                    Text dateFinText = new Text("Date Fin: " + publicite.getDateFin());
                    dateFinText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #ffffff;");
                    Text supportText = new Text("Support: " + publicite.getSupport());
                    supportText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #ffffff;");
                    Text montantText = new Text("Montant: " + publicite.getMontant());
                    montantText.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-fill: #ffffff;");

                    gridPane.add(demandeIdText, 0, 0);
                    gridPane.add(dateDebutText, 1, 0);
                    gridPane.add(dateFinText, 0, 1);
                    gridPane.add(supportText, 1, 1);
                    gridPane.add(montantText, 0, 2);

                    vbox.getChildren().add(gridPane);

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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des publicités : " + e.getMessage());
            alert.showAndWait();
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
            String fxmlPath = "/FrontZ.fxml";
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
            System.out.println("Navigated back to FrontZ.fxml from PubliciteClient");
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du retour au tableau de bord : " + e.getMessage());
            alert.showAndWait();
            System.err.println("Error loading FrontZ.fxml: " + e.getMessage());
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

    @FXML
    public void onAjouterDemandeClick(ActionEvent event) {
        try {
            String fxmlPath = "/NosSalles.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find resource: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Nos Salles");
            stage.show();
            System.out.println("Navigated to NosSalles.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la navigation vers Nos Salles : " + e.getMessage());
            alert.showAndWait();
            System.err.println("Error loading NosSalles.fxml: " + e.getMessage());
        }
    }
}