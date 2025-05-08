package tn.cinema.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.embed.swing.SwingNode;
import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import javafx.util.Duration;
import tn.cinema.services.CourService;
import tn.cinema.entities.Cour;

public class StatsCoursChartController implements Initializable {

    @FXML
    private SwingNode participationChart;

    @FXML
    private Button retourButton;

    private PiePlot piePlot;
    private double rotationAngle = 0;
    private CourService courService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courService = new CourService();
        try {

            Map<Integer, Integer> participationsMap = courService.recupererToutesParticipations();
            System.out.println("Participations globales récupérées : " + participationsMap);

            if (participationsMap.isEmpty()) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Aucune participation");
                    alert.setHeaderText(null);
                    alert.setContentText("Aucune participation enregistrée dans la base de données.");
                    alert.showAndWait();
                });
                displayEmptyPieChart();
                return;
            }


            List<Cour> allCourses = courService.recuperer();
            Map<Integer, Cour> courseMap = new HashMap<>();
            for (Cour cour : allCourses) {
                courseMap.put(cour.getId(), cour);
            }


            DefaultPieDataset dataset = new DefaultPieDataset();
            int totalParticipations = participationsMap.values().stream().mapToInt(Integer::intValue).sum();

            for (Map.Entry<Integer, Integer> entry : participationsMap.entrySet()) {
                int courseId = entry.getKey();
                int participations = entry.getValue();
                Cour cour = courseMap.get(courseId);
                String courseLabel = cour != null ? cour.getTypeCour() : "Cours " + courseId;
                double percentage = (participations * 100.0) / totalParticipations;
                dataset.setValue(
                        String.format("%s (ID: %d, %d participant(s), %.1f%%)", courseLabel, courseId, participations, percentage),
                        participations
                );
            }

            displayPieChart(dataset);

        } catch (SQLException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de la récupération des données : " + e.getMessage());
                alert.showAndWait();
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors du traitement des données : " + e.getMessage());
                alert.showAndWait();
            });
        }
    }

    private void displayPieChart(DefaultPieDataset dataset) {
        JFreeChart pieChart = ChartFactory.createPieChart(
                "", // Titre supprimé
                dataset,
                true,
                true,
                false
        );

        piePlot = (PiePlot) pieChart.getPlot();
        piePlot.setBackgroundPaint(new Color(11, 15, 41));
        piePlot.setOutlinePaint(Color.WHITE);
        piePlot.setShadowPaint(new Color(0, 0, 0, 100));
        piePlot.setShadowXOffset(5);
        piePlot.setShadowYOffset(5);
        piePlot.setStartAngle(290);
        piePlot.setInteriorGap(0.02);
        piePlot.setLabelBackgroundPaint(new Color(62, 32, 99));
        piePlot.setLabelPaint(Color.WHITE);
        piePlot.setLabelShadowPaint(new Color(0, 0, 0, 50));

        int[] colors = {0xFF6F61, 0x6B5B95, 0x88B04B, 0xF7CAC9, 0x92A8D1};
        for (int i = 0; i < dataset.getItemCount(); i++) {
            piePlot.setSectionPaint(dataset.getKey(i), new Color(colors[i % colors.length]));
        }

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(900, 393));
        chartPanel.setMouseWheelEnabled(true);

        chartPanel.addChartMouseListener(new org.jfree.chart.ChartMouseListener() {
            @Override
            public void chartMouseClicked(org.jfree.chart.ChartMouseEvent event) {
                if (event.getEntity() instanceof org.jfree.chart.entity.PieSectionEntity) {
                    org.jfree.chart.entity.PieSectionEntity section = (org.jfree.chart.entity.PieSectionEntity) event.getEntity();
                    String sectionKey = (String) section.getSectionKey();
                    int participations = ((Number) dataset.getValue(sectionKey)).intValue();
                    String courseIdStr = sectionKey.split("ID: ")[1].split(",")[0];
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Détails du Cours");
                        alert.setHeaderText(null);
                        alert.setContentText("Cours ID: " + courseIdStr + "\nParticipants: " + participations);
                        alert.showAndWait();
                    });
                }
            }

            @Override
            public void chartMouseMoved(org.jfree.chart.ChartMouseEvent event) {

            }
        });

        Platform.runLater(() -> participationChart.setContent(chartPanel));

        Timeline rotationTimeline = new Timeline(
                new KeyFrame(Duration.millis(50), e -> {
                    rotationAngle += 1;
                    if (rotationAngle >= 360) rotationAngle = 0;
                    piePlot.setStartAngle(rotationAngle);
                })
        );
        rotationTimeline.setCycleCount(Animation.INDEFINITE);
        rotationTimeline.play();
    }

    private void displayEmptyPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Aucune donnée", 1);
        JFreeChart pieChart = ChartFactory.createPieChart(
                "", // Titre supprimé
                dataset,
                true,
                true,
                false
        );
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(new Color(11, 15, 41));
        plot.setOutlinePaint(Color.WHITE);
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(900, 393));
        Platform.runLater(() -> participationChart.setContent(chartPanel));
    }

    @FXML
    private void handleRetourToGestionCour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionCour.fxml"));
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors du retour : " + e.getMessage());
            alert.showAndWait();
        }
    }
}