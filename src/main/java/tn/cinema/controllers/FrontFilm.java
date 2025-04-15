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

public class FrontFilm {

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

    @FXML
    void goToDemandeClient(ActionEvent event) {
        // Handle Demande Client button click
    }

    @FXML
    void goToPubliciteClient(ActionEvent event) {
        // Handle Publicité Client button click
    }

    @FXML
    void logout(ActionEvent event) {
        // Handle logout
    }

    @FXML
    void toggleSubButtons(ActionEvent event) {
        // Handle toggle sub-buttons
    }

    // Method to populate the ListView with films
    private void populateFilmList(List<Films> filmsList) {
        // Adding films to the ListView
        list.getItems().addAll(filmsList);

        // Set custom cell factory to display film details
        list.setCellFactory(new Callback<ListView<Films>, javafx.scene.control.ListCell<Films>>() {
            @Override
            public javafx.scene.control.ListCell<Films> call(ListView<Films> param) {
                return new javafx.scene.control.ListCell<Films>() {
                    @Override
                    protected void updateItem(Films item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            // Main container
                            HBox hbox = new HBox(15);
                            hbox.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-background-radius: 10;");
                            hbox.setPrefHeight(80);

                            // Image
                            ImageView imageView = new ImageView();
                            try {
                                Image image = new Image("file:" + item.getImg(), 60, 60, true, true);
                                imageView.setImage(image);
                            } catch (Exception e) {
                                imageView.setImage(new Image("file:src/images/placeholder.jpg", 60, 60, true, true));
                            }
                            imageView.setFitHeight(60);
                            imageView.setFitWidth(60);
                            imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1); -fx-background-radius: 10;");

                            // Text container
                            VBox textContainer = new VBox(5);
                            Text title = new Text(item.getNom_film());
                            title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                            Text director = new Text("Réalisateur: " + item.getRealisateur());
                            director.setStyle("-fx-font-size: 13px; -fx-fill: #555;");

                            Text genre = new Text("Genre: " + item.getGenre());
                            genre.setStyle("-fx-font-size: 13px; -fx-fill: #777;");

                            textContainer.getChildren().addAll(title, director, genre);

                            // Combine everything
                            hbox.getChildren().addAll(imageView, textContainer);

                            setGraphic(hbox);
                        } else {
                            setGraphic(null);
                        }
                    }

                };
            }
        });
    }


    private FilmsService fs = new FilmsService();

    @FXML
    public void initialize() {
        try {
            // Retrieve the list of films from the database (like in AfficherFilm)
            List<Films> films = fs.recuperer();  // Assuming fs is your service class to interact with the database

            // Convert the list of films into an ObservableList
            ObservableList<Films> observableList = FXCollections.observableList(films);

            // Populate the ListView with the observable list
            list.setItems(observableList);

            // Set custom cell factory for displaying film details (name, director, genre, image)
            list.setCellFactory(new Callback<ListView<Films>, javafx.scene.control.ListCell<Films>>() {
                @Override
                public javafx.scene.control.ListCell<Films> call(ListView<Films> param) {
                    return new javafx.scene.control.ListCell<Films>() {
                        @Override
                        protected void updateItem(Films item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                HBox hbox = new HBox(10);
                                hbox.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-background-radius: 10;");
                                hbox.setPrefHeight(100);

                                // Film Image
                                ImageView imageView = new ImageView();
                                try {
                                    Image image = new Image("file:" + item.getImg());
                                    imageView.setImage(image);
                                } catch (Exception e) {
                                    imageView.setImage(new Image("file:src/images/placeholder.jpg"));
                                }
                                imageView.setFitHeight(80);
                                imageView.setFitWidth(60);

                                // Text Info
                                VBox textBox = new VBox(5);
                                Text title = new Text(item.getNom_film());
                                title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                                Text director = new Text("Réalisateur: " + item.getRealisateur());
                                Text genre = new Text("Genre: " + item.getGenre());
                                textBox.getChildren().addAll(title, director, genre);

                                // Spacer to push button to the right
                                Region spacer = new Region();
                                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                                // Right-side Button
                                Button afficherProjectionBtn = new Button("Afficher Projection");
                                afficherProjectionBtn.setStyle("-fx-background-color: #3e2063; -fx-text-fill: white; -fx-font-size: 12px;");
                                afficherProjectionBtn.setOnAction(e -> {
                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontProjection.fxml"));
                                        Parent root = loader.load();

                                        // Optionally pass the selected film to the next controller
                                        // FrontProjection controller = loader.getController();
                                        // controller.setFilm(item); // if you want to pass the selected film

                                        Stage stage = (Stage) list.getScene().getWindow(); // Gets the current stage
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


                                // Combine all in HBox
                                hbox.getChildren().addAll(imageView, textBox, spacer, afficherProjectionBtn);
                                setGraphic(hbox);
                            } else {
                                setGraphic(null);
                            }
                        }
                    };
                }
            });
        } catch (SQLException e) {
            // Handle any database errors
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


}
