package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Films;
import tn.cinema.services.FilmsService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AfficherFilm extends Dashboard {

    @FXML
    private ListView<Films> listFilm;

    @FXML
    private TextField searchField;

    @FXML
    private Button statButton;

    private FilmsService fs = new FilmsService();
    private ObservableList<Films> allFilms;

    @FXML
    void ajout(ActionEvent event) {
        try {
            // Load the AjouterFilm.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFilm.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event (the current window)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new root to the current stage (i.e., replace the current content with the Add Film view)
            currentStage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setContentText("Impossible d'ouvrir la page d'ajout de film.");
            alert.showAndWait();
        }
    }

    @FXML
    void delete(ActionEvent event) {
        Films selectedFilm = listFilm.getSelectionModel().getSelectedItem();

        if (selectedFilm == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun film sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un film à supprimer.");
            alert.showAndWait();
            return;
        }

        try {
            fs.supprimer(selectedFilm.getId()); // delete by ID

            // Refresh the list
            List<Films> films = fs.recuperer();
            allFilms = FXCollections.observableArrayList(films);
            listFilm.setItems(allFilms);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Film supprimé avec succès !");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la suppression : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void edit(ActionEvent event) {
        Films selectedFilm = listFilm.getSelectionModel().getSelectedItem();

        if (selectedFilm == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun film sélectionné");
            alert.setContentText("Veuillez sélectionner un film à modifier.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFilm.fxml"));
            Parent root = loader.load();

            // Pass film to controller
            AjouterFilm controller = loader.getController();
            controller.edit(selectedFilm);

            listFilm.getScene().setRoot(root); // Navigate to form
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showStats(ActionEvent event) {
        try {
            // Fetch all films
            List<Films> films = fs.recuperer();

            // Count movies by genre
            Map<String, Integer> genreCounts = new HashMap<>();
            String[] genres = {"Action", "Horror", "Comidie", "Science Fiction", "Drama", "Romance"};
            for (String genre : genres) {
                genreCounts.put(genre, 0);
            }
            for (Films film : films) {
                String genre = film.getGenre();
                if (genreCounts.containsKey(genre)) {
                    genreCounts.put(genre, genreCounts.get(genre) + 1);
                }
            }

            // Create PieChart
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : genreCounts.entrySet()) {
                if (entry.getValue() > 0) { // Only include genres with at least one movie
                    pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }
            }
            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Statistiques des films par genre");
            pieChart.setLabelsVisible(true);
            pieChart.setLegendVisible(true);

            // Style the chart
            pieChart.setStyle("-fx-background-color: #0b0f29; -fx-font-size: 14px;");
            pieChart.lookup(".chart-title").setStyle("-fx-text-fill: white;");
            pieChart.lookup(".chart-legend").setStyle("-fx-text-fill: white; -fx-background-color: #021b50;");
            // Style pie slice labels
            pieChart.getData().forEach(data -> {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-label-visible: true;");
                    Node label = data.getNode().lookup(".chart-pie-label");
                    if (label != null) {
                        label.setStyle("-fx-fill: white; -fx-font-size: 12px;");
                    }
                }
            });

            // Create a new stage for the chart
            Stage statsStage = new Stage();
            statsStage.setTitle("Statistiques des films");
            VBox chartContainer = new VBox(pieChart);
            chartContainer.setStyle("-fx-background-color: #0b0f29; -fx-padding: 20;");
            Scene scene = new Scene(chartContainer, 600, 400);
            statsStage.setScene(scene);
            statsStage.show();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de la récupération des films : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void initialize() {
        try {
            // Retrieve the list of films from the database
            List<Films> films = fs.recuperer();
            allFilms = FXCollections.observableArrayList(films);
            listFilm.setItems(allFilms);

            // Set custom cell factory for ListView
            listFilm.setCellFactory(param -> new javafx.scene.control.ListCell<Films>() {
                @Override
                protected void updateItem(Films item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        // Main VBox container for vertical layout
                        VBox mainVBox = new VBox(10);
                        mainVBox.setStyle("-fx-padding: 10; -fx-alignment: center;");

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
                        imageView.setFitHeight(250); // Increased image size
                        imageView.setFitWidth(200);  // Increased image size
                        imageView.setPreserveRatio(true);

                        imageHBox.getChildren().add(imageView);

                        // Text content
                        VBox textVBox = new VBox(5);
                        textVBox.setStyle("-fx-padding: 5; -fx-alignment: center;");

                        Text title = new Text(item.getNom_film());
                        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: white;");

                        Text director = new Text("Réalisateur: " + item.getRealisateur());
                        director.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-fill: #d3b4b4;");

                        Text genre = new Text("Genre: " + item.getGenre());
                        genre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-fill: #d3b4b4;");

                        textVBox.getChildren().addAll(title, director, genre);

                        // Add image and text to main VBox
                        mainVBox.getChildren().addAll(imageHBox, textVBox);
                        setGraphic(mainVBox);
                    } else {
                        setGraphic(null);
                    }
                }
            });

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

    private void filterFilms(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            listFilm.setItems(allFilms);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            ObservableList<Films> filteredList = allFilms.stream()
                    .filter(film ->
                            film.getNom_film().toLowerCase().contains(lowerCaseFilter) ||
                                    film.getRealisateur().toLowerCase().contains(lowerCaseFilter) ||
                                    film.getGenre().toLowerCase().contains(lowerCaseFilter)
                    )
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            listFilm.setItems(filteredList);
        }
    }

    @FXML
    void gestionFilm(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionFilms.fxml")); // Adjust path if needed
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();   // Get current window
            Scene scene = new Scene(root);
            stage.setScene(scene); // Set the new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // For debug
        }
    }
}