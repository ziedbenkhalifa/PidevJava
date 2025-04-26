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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Projection;
import tn.cinema.services.ProjectionService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @FXML
    private Button timelineButton;

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

    @FXML
    void showTimeline(ActionEvent event) {
        try {
            // Fetch projections
            List<Projection> projections = ps.recuperer();
            if (projections.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Aucune projection");
                alert.setContentText("Aucune projection à afficher dans la timeline.");
                alert.showAndWait();
                return;
            }

            // Determine date range
            LocalDate today = LocalDate.now();
            LocalDate minDate = projections.stream()
                    .map(Projection::getDate_projection)
                    .min(LocalDate::compareTo)
                    .orElse(today);
            LocalDate maxDate = projections.stream()
                    .map(Projection::getDate_projection)
                    .max(LocalDate::compareTo)
                    .orElse(today);
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(minDate, maxDate) + 1;

            // Create timeline pane
            Pane timelinePane = new Pane();
            timelinePane.setStyle("-fx-background-color: #0b0f29;");
            double paneWidth = Math.max(600, daysBetween * 100); // 100px per day
            double paneHeight = projections.size() * 60 + 60; // 60px per projection + margin
            timelinePane.setPrefSize(paneWidth, paneHeight);

            // Zoom support
            final double[] scale = {1.0};
            timelinePane.setOnScroll((ScrollEvent scrollEvent) -> {
                double deltaY = scrollEvent.getDeltaY();
                scale[0] = Math.max(0.5, Math.min(2.0, scale[0] + deltaY * 0.001));
                timelinePane.setScaleX(scale[0]);
                timelinePane.setScaleY(scale[0]);
            });

            // Track rectangles for selection
            List<Rectangle> rectangles = new ArrayList<>();

            // Add projections as rectangles
            for (int i = 0; i < projections.size(); i++) {
                Projection p = projections.get(i);
                LocalDate date = p.getDate_projection();

                // Determine color based on date
                String color;
                if (date.isAfter(today)) {
                    color = "#2ecc71"; // Brighter green for future
                } else if (date.isBefore(today)) {
                    color = "#ff0000"; // Bright red for past
                } else {
                    color = "#ffd700"; // Gold yellow for today
                }

                // Calculate x position (days since minDate)
                long daysFromStart = java.time.temporal.ChronoUnit.DAYS.between(minDate, date);
                double x = daysFromStart * 100 + (i % 5) * 20; // Adjusted offset
                double y = i * 60 + 30; // Increased vertical spacing
                double width = 60; // Larger width
                double height = 35; // Larger height

                Rectangle rect = new Rectangle(x, y, width, height);
                rect.setFill(Paint.valueOf(color));
                rect.setStroke(Paint.valueOf("white")); // White border
                rect.setStrokeWidth(0.5);
                rect.setArcWidth(15); // Smoother corners
                rect.setArcHeight(15);
                rectangles.add(rect);

                // Tooltip
                Tooltip tooltip = new Tooltip(
                        "Date: " + p.getDate_projection() +
                                "\nCapacité: " + p.getCapaciter() +
                                "\nPrix: " + p.getPrix() + " DT"
                );
                Tooltip.install(rect, tooltip);

                // Click to select in ListView (left-click)
                int finalI = i;
                rect.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        // Select in ListView
                        listProjection.getSelectionModel().select(finalI);
                        listProjection.scrollTo(finalI);
                        // Highlight selected rectangle
                        rectangles.forEach(r -> r.setStrokeWidth(0.5)); // Reset all
                        rect.setStrokeWidth(2.0); // Thicker border for selected
                    }
                });

                // Context menu for all projections (right-click)
                ContextMenu contextMenu = new ContextMenu();
                MenuItem deleteItem = new MenuItem("Supprimer");
                deleteItem.setOnAction(e -> {
                    try {
                        ps.supprimer(p.getId()); // Delete from database
                        // Refresh ListView
                        List<Projection> updatedProjections = ps.recuperer();
                        allProjections.setAll(updatedProjections);
                        listProjection.setItems(allProjections);
                        // Refresh timeline
                        Stage stage = (Stage) timelinePane.getScene().getWindow();
                        stage.close(); // Close current timeline
                        showTimeline(event); // Reopen with updated data
                        // Show success alert
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Succès");
                        alert.setContentText("Projection supprimée avec succès !");
                        alert.showAndWait();
                    } catch (SQLException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setContentText("Erreur lors de la suppression : " + ex.getMessage());
                        alert.showAndWait();
                    }
                });
                contextMenu.getItems().add(deleteItem);
                rect.setOnContextMenuRequested(e -> contextMenu.show(rect, e.getScreenX(), e.getScreenY()));

                // Label above rectangle
                Text label = new Text(x + 5, y - 5, p.getDate_projection().toString());
                label.setFill(Paint.valueOf(color));
                label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

                timelinePane.getChildren().addAll(rect, label);
            }

            // Add date labels on x-axis
            for (long d = 0; d <= daysBetween; d++) {
                LocalDate date = minDate.plusDays(d);
                String color;
                if (date.isAfter(today)) {
                    color = "#2ecc71"; // Brighter green for future
                } else if (date.isBefore(today)) {
                    color = "#ff0000"; // Bright red for past
                } else {
                    color = "#ffd700"; // Gold yellow for today
                }
                Text dateLabel = new Text(d * 100 + 10, paneHeight - 20, date.toString());
                dateLabel.setFill(Paint.valueOf(color));
                dateLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
                timelinePane.getChildren().add(dateLabel);
            }

            // Create scrollable timeline
            ScrollPane scrollPane = new ScrollPane(timelinePane);
            scrollPane.setFitToHeight(true);
            scrollPane.setStyle("-fx-background-color: #0b0f29;");

            // Display in a new stage
            Stage timelineStage = new Stage();
            timelineStage.setTitle("Timeline des projections");
            Scene scene = new Scene(scrollPane, 600, 400);
            timelineStage.setScene(scene);
            timelineStage.show();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors du chargement de la timeline : " + e.getMessage());
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