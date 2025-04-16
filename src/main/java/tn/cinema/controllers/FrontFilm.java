package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Films;
import javafx.util.Callback;
import tn.cinema.services.FilmsService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FrontFilm extends FrontzController {

    @FXML
    private Button coursButton;

    @FXML
    private Button demandeSubButton;

    @FXML
    private Button filmsButton;

    @FXML
    private ListView<Films> list;

    @FXML
    private Button logoutButton;

    @FXML
    private Button monCompteButton;

    @FXML
    private Button produitsButton;

    @FXML
    private Button publiciteSubButton;

    @FXML
    private Button publicitesButton;





    private FilmsService fs = new FilmsService();

    @FXML
    public void initialize() {
        try {
            // Retrieve the list of films from the database
            List<Films> films = fs.recuperer();
            ObservableList<Films> observableList = FXCollections.observableList(films);
            list.setItems(observableList);

            // Set custom cell factory for displaying film details
            list.setCellFactory(new Callback<ListView<Films>, javafx.scene.control.ListCell<Films>>() {
                @Override
                public javafx.scene.control.ListCell<Films> call(ListView<Films> param) {
                    return new javafx.scene.control.ListCell<Films>() {
                        @Override
                        protected void updateItem(Films item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null && !empty) {
                                // Main VBox container for vertical layout
                                VBox mainVBox = new VBox(10);
                                mainVBox.setStyle(
                                        "-fx-background-color: linear-gradient(to right, #0b0f29, #0b0f29);" +
                                                "-fx-background-radius: 12;" +
                                                "-fx-border-radius: 12;" +
                                                "-fx-border-color: #0b0f29;" +
                                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);"
                                );

                                // HBox for centering the image
                                HBox imageHBox = new HBox();
                                imageHBox.setStyle("-fx-alignment: center;");

                                // ImageView
                                ImageView imageView = new ImageView();
                                try {
                                    Image image = new Image("file:" + item.getImg());
                                    imageView.setImage(image);
                                } catch (Exception e) {
                                    imageView.setImage(new Image("file:src/images/placeholder.jpg"));
                                }
                                imageView.setFitHeight(240); // Match AfficherFilm image size
                                imageView.setFitWidth(200);  // Match AfficherFilm image size
                                imageView.setPreserveRatio(true);

                                imageHBox.getChildren().add(imageView);

                                // Text content
                                VBox textVBox = new VBox(5);
                                textVBox.setStyle("-fx-padding: 5; -fx-alignment: center;");

                                Text title = new Text(item.getNom_film());
                                title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: white;");

                                Text director = new Text("Réalisateur: " + item.getRealisateur());
                                director.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-fill: white;");

                                Text genre = new Text("Genre: " + item.getGenre());
                                genre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-fill: white;");

                                textVBox.getChildren().addAll(title, director, genre);

                                // Afficher Projection Button
                                Button afficherProjectionBtn = new Button("Afficher Projection");
                                afficherProjectionBtn.setStyle("-fx-background-color: #3e2063; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 5;");
                                afficherProjectionBtn.setOnAction(e -> {
                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontProjection.fxml"));
                                        Parent root = loader.load();
                                        Stage stage = (Stage) list.getScene().getWindow();
                                        stage.setScene(new Scene(root));
                                        stage.show();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Erreur de navigation");
                                        alert.setHeaderText("Impossible de charger la page des projections");
                                        alert.setContentText(ex.getMessage());
                                        alert.showAndWait();
                                    }
                                });

                                // Add image, text, and button to main VBox
                                mainVBox.getChildren().addAll(imageHBox, textVBox, afficherProjectionBtn);
                                setGraphic(mainVBox);
                            } else {
                                setGraphic(null);
                            }
                        }
                    };
                }
            });
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}