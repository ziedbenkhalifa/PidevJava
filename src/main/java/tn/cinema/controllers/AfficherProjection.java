package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Projection;
import tn.cinema.services.ProjectionService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AfficherProjection extends Dashboard {

    @FXML
    private ListView<Projection> listProjection;

    @FXML
    private TextField searchField;

    @FXML
    private Button statButton;

    private ProjectionService ps = new ProjectionService();
    private ObservableList<Projection> allProjections;

    @FXML
    void initialize() {
        try {
            // Retrieve the list of projections from the database
            List<Projection> projections = ps.recuperer();
            allProjections = FXCollections.observableArrayList(projections);
            listProjection.setItems(allProjections);

            // Set custom cell factory
            listProjection.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Projection item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        // HBox container
                        HBox hbox = new HBox(10);
                        hbox.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

                        // VBox for text content
                        VBox vbox = new VBox(5);
                        vbox.setStyle("-fx-padding: 5;");

                        // Displaying date, capaciter, and prix
                        Text date = new Text("Date: " + item.getDate_projection());
                        date.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: black;");

                        Text capacite = new Text("Capacité: " + item.getCapaciter());
                        capacite.setStyle("-fx-font-size: 13px; -fx-fill: black;");

                        Text prix = new Text("Prix: " + item.getPrix() + " DT");
                        prix.setStyle("-fx-font-size: 13px; -fx-fill: black;");

                        vbox.getChildren().addAll(date, capacite, prix);
                        hbox.getChildren().add(vbox);

                        setGraphic(hbox);  // Set the graphic for each item in the list
                    } else {
                        setGraphic(null);  // Set null when the item is empty
                    }
                }
            });

            // Add dynamic search functionality
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterProjections(newValue);
            });

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur de récupération des projections : " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Filter projections based on search text
    private void filterProjections(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            listProjection.setItems(allProjections);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            ObservableList<Projection> filteredList = allProjections.stream()
                    .filter(projection ->
                            projection.getDate_projection().toString().toLowerCase().contains(lowerCaseFilter) ||
                                    String.valueOf(projection.getCapaciter()).contains(lowerCaseFilter) ||
                                    String.valueOf(projection.getPrix()).contains(lowerCaseFilter)
                    )
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            listProjection.setItems(filteredList);
        }
    }

    @FXML
    void showStats(ActionEvent event) {
        try {
            // Fetch all projections
            List<Projection> projections = ps.recuperer();

            // Count projections by date
            Map<String, Integer> dateCounts = projections.stream()
                    .collect(Collectors.groupingBy(
                            projection -> projection.getDate_projection().toString(),
                            Collectors.summingInt(e -> 1)
                    ));

            // Create BarChart
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Date");
            yAxis.setLabel("Nombre de projections");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Nombre de projections par jour");

            // Add data to series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Projections");
            dateCounts.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0) // Only include dates with at least one projection
                    .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
            barChart.getData().add(series);

            // Style the chart
            barChart.setStyle("-fx-background-color: #0b0f29; -fx-font-size: 14px;");
            barChart.lookup(".chart-title").setStyle("-fx-text-fill: white;");
            barChart.lookup(".chart-legend").setStyle("-fx-text-fill: white; -fx-background-color: #021b50;");
            xAxis.setStyle("-fx-tick-label-fill: white;");
            yAxis.setStyle("-fx-tick-label-fill: white;");
            // Style bars
            series.getData().forEach(data -> {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: #3e2063;");
                }
            });

            // Create a new stage for the chart
            Stage statsStage = new Stage();
            statsStage.setTitle("Statistiques des projections");
            VBox chartContainer = new VBox(barChart);
            chartContainer.setStyle("-fx-background-color: #0b0f29; -fx-padding: 20;");
            Scene scene = new Scene(chartContainer, 600, 400);
            statsStage.setScene(scene);
            statsStage.show();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de la récupération des projections : " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Navigate to the 'AjouterProjection' form
    @FXML
    void ajout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProjection.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new root to the current stage
            currentStage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setContentText("Impossible d'ouvrir la page d'ajout de projection.");
            alert.showAndWait();
        }
    }

    // Delete a selected projection
    @FXML
    void delete(ActionEvent event) {
        Projection selectedProjection = listProjection.getSelectionModel().getSelectedItem();

        if (selectedProjection == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune projection sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une projection à supprimer.");
            alert.showAndWait();
            return;
        }

        try {
            ps.supprimer(selectedProjection.getId()); // Delete projection by ID

            // Refresh the list
            List<Projection> projections = ps.recuperer();
            allProjections.setAll(projections);
            listProjection.setItems(allProjections);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Projection supprimée avec succès !");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la suppression : " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Navigate to the form to edit a selected projection
    @FXML
    void edit(ActionEvent event) {
        Projection selectedProjection = listProjection.getSelectionModel().getSelectedItem();

        if (selectedProjection == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune projection sélectionnée");
            alert.setContentText("Veuillez sélectionner une projection à modifier.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProjection.fxml"));
            Parent root = loader.load();

            // Pass projection to the form controller
            AjouterProjection controller = loader.getController();
            controller.edit(selectedProjection);

            listProjection.getScene().setRoot(root); // Navigate to form
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Navigate to another management screen
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