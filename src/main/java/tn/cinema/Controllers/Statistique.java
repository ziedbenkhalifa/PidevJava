package tn.cinema.Controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tn.cinema.entities.Salle;
import tn.cinema.entities.Equipement;
import tn.cinema.services.SalleService;
import tn.cinema.services.EquipementService;
import tn.cinema.services.SalleStats;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Statistique implements Initializable {

    @FXML
    private VBox chartContainer;

    private final SalleService salleService = new SalleService();
    private final EquipementService equipementService = new EquipementService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        afficherStatistiques();
    }

    private void afficherStatistiques() {
        chartContainer.getChildren().clear();

        try {
            // Partie Salle
            List<Salle> salles = salleService.recuperer();
            Label salleLabel = createTitleLabel("📊 Répartition des salles selon leur état");
            chartContainer.getChildren().add(salleLabel);

            if (salles.isEmpty()) {
                afficherMessage("Aucune salle trouvée.");
            } else {
                PieChart pieChart = createPieChartSalles(salles);
                chartContainer.getChildren().add(pieChart);
                afficherTotal("Nombre total de salles : " + salles.size());
            }

            // Partie Equipement
            List<Equipement> equipements = equipementService.recuperer();
            Label equipementLabel = createTitleLabel("📊 Répartition des équipements selon leur état");
            chartContainer.getChildren().add(equipementLabel);

            if (equipements.isEmpty()) {
                afficherMessage("Aucun équipement trouvé.");
            } else {
                BarChart<String, Number> barChart = createBarChartEquipements(equipements);
                chartContainer.getChildren().add(barChart);
                afficherTotal("Nombre total d'équipements : " + equipements.size());
            }

            afficherRafraichirButton();

        } catch (SQLException e) {
            afficherErreur(e);
        }
    }

    private Label createTitleLabel(String texte) {
        Label label = new Label(texte);
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2A3B77;");
        VBox.setMargin(label, new Insets(15, 0, 0, 0));
        return label;
    }

    private void afficherMessage(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");
        VBox.setMargin(label, new Insets(5, 0, 10, 0));
        chartContainer.getChildren().add(label);
    }

    private void afficherTotal(String texte) {
        Label label = new Label(texte);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        VBox.setMargin(label, new Insets(10, 0, 0, 0));
        chartContainer.getChildren().add(label);
    }

    private void afficherRafraichirButton() {
        Button refreshButton = new Button("🔄 Rafraîchir les stats");
        refreshButton.setStyle("-fx-background-color: #2A3B77; -fx-text-fill: white; -fx-padding: 5px 15px; -fx-font-weight: bold; -fx-background-radius: 5;");
        VBox.setMargin(refreshButton, new Insets(15, 0, 10, 0));
        refreshButton.setOnAction(e -> afficherStatistiques());
        chartContainer.getChildren().add(refreshButton);
    }

    private void afficherErreur(SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur SQL");
        alert.setHeaderText("Une erreur s'est produite lors de la récupération des données.");
        alert.setContentText(e.getMessage());
        alert.showAndWait();

        System.err.println("Erreur SQL : " + e.getMessage());
        e.printStackTrace();
    }

    private PieChart createPieChartSalles(List<Salle> salles) {
        PieChart pieChart = SalleStats.createEtatSalleChart(salles);
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(Side.BOTTOM);
        pieChart.setClockwise(true);

        // Appliquer les couleurs personnalisées aux slices
        String[] couleurs = {"#ff7eb6", "#7fdbda", "#d3d3d3"}; // rose, turquoise, gris clair
        int i = 0;
        for (PieChart.Data data : pieChart.getData()) {
            String couleur = couleurs[i % couleurs.length];
            data.getNode().setStyle("-fx-pie-color: " + couleur + ";");

            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue() + " salles");
            Tooltip.install(data.getNode(), tooltip);
            i++;
        }

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), pieChart);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        return pieChart;
    }

    private BarChart<String, Number> createBarChartEquipements(List<Equipement> equipements) {
        Map<String, Long> repartition = equipements.stream()
                .collect(Collectors.groupingBy(Equipement::getEtat, Collectors.counting()));

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("État");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre d'équipements");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String[] couleurs = {"#7fdbda", "#ff7eb6", "#a6a6a6"}; // turquoise, rose, gris
        int colorIndex = 0;

        for (Map.Entry<String, Long> entry : repartition.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());

            // Ajoute listener pour changer la couleur après le rendu du node
            final String couleur = couleurs[colorIndex % couleurs.length];
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + couleur + ";");
                }
            });

            series.getData().add(data);
            colorIndex++;
        }

        barChart.getData().add(series);

        FadeTransition ft = new FadeTransition(Duration.seconds(1), barChart);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return barChart;
    }
}
