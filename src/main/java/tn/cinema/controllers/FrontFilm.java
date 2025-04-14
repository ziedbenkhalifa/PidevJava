package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import tn.cinema.entities.Films;
import javafx.util.Callback;
import tn.cinema.services.FilmsService;

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
                                // Create HBox to hold the film details and image
                                HBox hbox = new HBox(10);

                                // Text for film details (name, director, genre)
                                Text text = new Text(item.getNom_film() + " - " + item.getRealisateur() + " - " + item.getGenre());

                                // Load the image (handle null cases if needed)
                                ImageView imageView = new ImageView();
                                try {
                                    // Assuming the image path is correct, you may need to adjust if images are stored in the resources folder
                                    Image image = new Image("file:" + item.getImg()); // Modify the path as needed
                                    imageView.setImage(image);
                                } catch (Exception e) {
                                    // Handle image loading error, show a placeholder if image fails to load
                                    imageView.setImage(new Image("file:src/images/placeholder.jpg")); // Use a placeholder image if necessary
                                }

                                // Set the image size
                                imageView.setFitHeight(100);
                                imageView.setFitWidth(80);


                                // Add the image and text to HBox
                                hbox.getChildren().addAll(imageView, text);

                                // Set the HBox as the cell's graphic
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
