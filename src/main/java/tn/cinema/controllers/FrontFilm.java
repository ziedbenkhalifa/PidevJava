package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Films;
import tn.cinema.services.FilmsService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class FrontFilm extends FrontzController {

    @FXML
    private Button coursButton;

    @FXML
    private Button demandeSubButton;

    @FXML
    private Button filmsButton;

    @FXML
    private GridPane filmsGrid;

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
    private TextField searchField;

    private FilmsService fs = new FilmsService();
    private ObservableList<Films> allFilms;

    @FXML
    public void initialize() {
        try {
            // Retrieve the list of films from the database
            List<Films> films = fs.recuperer();
            allFilms = FXCollections.observableArrayList(films);
            populateGrid(allFilms);

            // Add dynamic search functionality
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterFilms(newValue);
            });

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void populateGrid(ObservableList<Films> films) {
        filmsGrid.getChildren().clear();
        int column = 0;
        int row = 0;
        int columnsPerRow = 2; // Set to 2 for two movies per row

        for (Films item : films) {
            // Main VBox container for vertical layout
            VBox mainVBox = new VBox(15);
            mainVBox.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #1a1e3a, #0b0f29);" +
                            "-fx-background-radius: 20;" +
                            "-fx-border-radius: 20;" +
                            "-fx-border-color: #3e2063;" +
                            "-fx-padding: 15;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 3);" +
                            "-fx-opacity: 1.0;"
            );
            mainVBox.setPrefWidth(400); // Increased width for larger cards in 2-column layout

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
            imageView.setFitHeight(300); // Larger image for better visibility
            imageView.setFitWidth(300);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);");

            imageHBox.getChildren().add(imageView);

            // Text content
            VBox textVBox = new VBox(8);
            textVBox.setStyle("-fx-padding: 8; -fx-alignment: center;");

            Text title = new Text(item.getNom_film());
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: white; -fx-text-alignment: center;");
            title.setWrappingWidth(300); // Wrap text to avoid overflow

            Text director = new Text("Réalisateur: " + item.getRealisateur());
            director.setStyle("-fx-font-size: 14px; -fx-fill: #d3b4b4; -fx-text-alignment: center;");
            director.setWrappingWidth(300);

            Text genre = new Text("Genre: " + item.getGenre());
            genre.setStyle("-fx-font-size: 14px; -fx-fill: #d3b4b4; -fx-text-alignment: center;");
            genre.setWrappingWidth(300);

            textVBox.getChildren().addAll(title, director, genre);

            // Afficher Projection Button
            Button afficherProjectionBtn = new Button("Afficher Projection");
            afficherProjectionBtn.setStyle(
                    "-fx-background-color: #3e2063;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 8 15 8 15;" +
                            "-fx-cursor: hand;"
            );
            afficherProjectionBtn.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontProjection.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) filmsGrid.getScene().getWindow();
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

            // Add to GridPane
            filmsGrid.add(mainVBox, column, row);
            column++;
            if (column == columnsPerRow) {
                column = 0;
                row++;
            }
        }

        // Set GridPane height to accommodate all rows for scrolling
        filmsGrid.setPrefHeight(Math.max(355.0, row * 450 + 60)); // Ensure at least the ScrollPane height
    }

    private void filterFilms(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            populateGrid(allFilms);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            ObservableList<Films> filteredList = allFilms.stream()
                    .filter(film ->
                            film.getNom_film().toLowerCase().contains(lowerCaseFilter) ||
                                    film.getRealisateur().toLowerCase().contains(lowerCaseFilter) ||
                                    film.getGenre().toLowerCase().contains(lowerCaseFilter)
                    )
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            populateGrid(filteredList);
        }
    }
}