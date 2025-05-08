package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Films;
import tn.cinema.services.FilmsService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FrontzController extends BaseController {

    @FXML
    private FlowPane filmFlowPane;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private Button publicitesButton;
    @FXML
    private Button demandeSubButton;
    @FXML
    private Button publiciteSubButton;

    @FXML
    private Button coursButton;
    @FXML
    private Button courSubButton;
    @FXML
    private Button seanceSubButton;

    private FilmsService fs = new FilmsService();

    @FXML
    public void initialize() {
        try {
            List<Films> films = fs.recuperer();

            for (Films item : films) {
                VBox mainVBox = new VBox(10);
                mainVBox.setStyle("-fx-padding: 10; -fx-alignment: center; -fx-background-color: transparent;");
                mainVBox.setPrefWidth(300);

                HBox imageHBox = new HBox();
                imageHBox.setStyle("-fx-alignment: center;");

                ImageView imageView = new ImageView();
                try {
                    Image image = new Image("file:" + item.getImg());
                    imageView.setImage(image);
                } catch (Exception e) {
                    imageView.setImage(new Image("file:src/images/placeholder.jpg"));
                }
                imageView.setFitHeight(200);
                imageView.setFitWidth(160);
                imageView.setPreserveRatio(true);

                imageHBox.getChildren().add(imageView);

                VBox textVBox = new VBox(5);
                textVBox.setStyle("-fx-padding: 5; -fx-alignment: center;");

                Text title = new Text(item.getNom_film());
                title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: white;");

                Text director = new Text("Réalisateur: " + item.getRealisateur());
                director.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-fill: white;");

                Text genre = new Text("Genre: " + item.getGenre());
                genre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-fill: white;");

                textVBox.getChildren().addAll(title, director, genre);

                mainVBox.getChildren().addAll(imageHBox, textVBox);

                filmFlowPane.getChildren().add(mainVBox);
            }

            filmFlowPane.needsLayoutProperty().addListener((obs, old, newValue) -> {
                if (!newValue) {
                    double contentHeight = filmFlowPane.getHeight();
                    if (contentHeight > 0) {
                        backgroundImageView.setFitHeight(Math.max(contentHeight, 355));
                    }
                }
            });

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }



    }

    @FXML
    private void handleMonCompteAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        super.handleMonCompteAction(stage);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PubliciteClient.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void filmsButtonClicked(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FrontFilm.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void affichage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListProduits.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la page des produits.");
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void toggleSubButtonss() {
        boolean areSubButtonsVisible = courSubButton.isVisible();
        courSubButton.setVisible(!areSubButtonsVisible);
        seanceSubButton.setVisible(!areSubButtonsVisible);
    }

    @FXML
    private void goCourAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) courSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation vers la page Cours : " + e.getMessage());
        }
    }

    @FXML
    private void handleSeanceAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichageListSeances.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) seanceSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation vers la page Séances : " + e.getMessage());
        }
    }

    private void showAlerty(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void handlePagePrincipale(ActionEvent event) {
        System.out.println("clic bouton");
        chargerPage("/tn/cinema/Views/Page1.fxml", "Page Principale", event);
    }


    // Méthode utilitaire pour charger une page et changer la scène
    private void chargerPage(String cheminFxml, String titrePage, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Page1.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(titrePage);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur de chargement", "Impossible de charger la page : " + titrePage);
        }
    }

    @FXML
    private void goSeanceAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListSeancesFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) seanceSubButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation vers la page Séances : " + e.getMessage());
        }
    }
}