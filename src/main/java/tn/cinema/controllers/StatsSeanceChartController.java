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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import javafx.util.Duration;
import tn.cinema.services.SeanceService;
import tn.cinema.entities.Seance;
import tn.cinema.entities.Cour;

public class StatsSeanceChartController implements Initializable {

    @FXML
    private SwingNode participationChart;

    @FXML
    private Button retourButton;

    @FXML
    private Label detailsLabel;

    private PiePlot3D piePlot;
    private double rotationAngle = 0;
    private SeanceService seanceService;
    private Map<String, Color> originalColors;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        seanceService = new SeanceService();
        originalColors = new HashMap<>();
        try {

            List<Seance> allSeances = seanceService.recuperer();
            if (allSeances.isEmpty()) {
                Platform.runLater(() -> {
                    detailsLabel.setText("Aucune séance enregistrée dans la base de données.");
                });
                displayEmptyPieChart();
                return;
            }

            Map<String, Integer> seanceByTypeCour = new HashMap<>();
            int totalSeances = allSeances.size();
            for (Seance seance : allSeances) {
                String typeCour = (seance.getCour() != null && seance.getCour().getTypeCour() != null)
                        ? seance.getCour().getTypeCour() : "Inconnu";
                seanceByTypeCour.put(typeCour, seanceByTypeCour.getOrDefault(typeCour, 0) + 1);
            }


            DefaultPieDataset dataset = new DefaultPieDataset();
            for (Map.Entry<String, Integer> entry : seanceByTypeCour.entrySet()) {
                String typeCour = entry.getKey();
                int count = entry.getValue();
                double percentage = (count * 100.0) / totalSeances;
                dataset.setValue(
                        String.format("%s (%d séance(s), %.1f%%)", typeCour, count, percentage),
                        count
                );
            }

            displayPieChart(dataset);

        } catch (SQLException e) {
            Platform.runLater(() -> {
                detailsLabel.setText("Erreur lors de la récupération des données : " + e.getMessage());
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                detailsLabel.setText("Une erreur s'est produite : " + e.getMessage());
            });
        }
    }

    private void displayPieChart(DefaultPieDataset dataset) {
        JFreeChart pieChart = ChartFactory.createPieChart3D(
                "", // Titre supprimé
                dataset,
                true,
                true,
                false
        );

        piePlot = (PiePlot3D) pieChart.getPlot();
        piePlot.setBackgroundPaint(new Color(15, 20, 50));
        piePlot.setOutlinePaint(Color.WHITE);
        piePlot.setShadowPaint(new Color(0, 0, 0, 150));
        piePlot.setDepthFactor(0.1); // Ajoute un effet 3D
        piePlot.setStartAngle(290);
        piePlot.setInteriorGap(0.02);
        piePlot.setLabelBackgroundPaint(new Color(80, 40, 120));
        piePlot.setLabelPaint(Color.WHITE);
        piePlot.setLabelShadowPaint(new Color(0, 0, 0, 80));


        Color[] colors = {
                new Color(255, 99, 71),  // Tomato
                new Color(106, 90, 205), // SlateBlue
                new Color(50, 205, 50),  // LimeGreen
                new Color(255, 215, 0),  // Gold
                new Color(138, 43, 226)  // BlueViolet
        };
        for (int i = 0; i < dataset.getItemCount(); i++) {
            String key = (String) dataset.getKey(i);
            Color sectionColor = colors[i % colors.length];
            piePlot.setSectionPaint(key, sectionColor);
            originalColors.put(key, sectionColor);
        }

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(900, 400));
        chartPanel.setMouseWheelEnabled(true);

        chartPanel.addChartMouseListener(new org.jfree.chart.ChartMouseListener() {
            @Override
            public void chartMouseClicked(org.jfree.chart.ChartMouseEvent event) {
                if (event.getEntity() instanceof org.jfree.chart.entity.PieSectionEntity) {
                    org.jfree.chart.entity.PieSectionEntity section = (org.jfree.chart.entity.PieSectionEntity) event.getEntity();
                    String sectionKey = (String) section.getSectionKey();
                    int seances = ((Number) dataset.getValue(sectionKey)).intValue();

                    Platform.runLater(() -> {
                        detailsLabel.setText("Type: " + sectionKey.split(" ")[0] + " | Séances: " + seances);
                    });


                    for (String key : originalColors.keySet()) {
                        piePlot.setSectionPaint(key, originalColors.get(key));
                    }
                    Color highlightColor = Color.YELLOW;
                    piePlot.setSectionPaint(sectionKey, highlightColor);


                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    piePlot.setSectionPaint(sectionKey, originalColors.get(sectionKey));
                                }
                            },
                            1000
                    );
                }
            }

            @Override
            public void chartMouseMoved(org.jfree.chart.ChartMouseEvent event) {

                if (event.getEntity() instanceof org.jfree.chart.entity.PieSectionEntity) {
                    org.jfree.chart.entity.PieSectionEntity section = (org.jfree.chart.entity.PieSectionEntity) event.getEntity();
                    String sectionKey = (String) section.getSectionKey();
                    piePlot.setSectionPaint(sectionKey, originalColors.get(sectionKey).brighter());
                }
            }
        });

        Platform.runLater(() -> participationChart.setContent(chartPanel));

        Timeline rotationTimeline = new Timeline(
                new KeyFrame(Duration.millis(50), e -> {
                    rotationAngle += 0.5;
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
        JFreeChart pieChart = ChartFactory.createPieChart3D(
                "", // Titre supprimé
                dataset,
                true,
                true,
                false
        );
        PiePlot3D plot = (PiePlot3D) pieChart.getPlot();
        plot.setBackgroundPaint(new Color(15, 20, 50));
        plot.setOutlinePaint(Color.WHITE);
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(900, 400));
        Platform.runLater(() -> participationChart.setContent(chartPanel));
    }

    @FXML
    private void handleRetourToGestionSeance(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionCour.fxml"));
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Platform.runLater(() -> {
                detailsLabel.setText("Erreur lors du retour : " + e.getMessage());
            });
        }
    }
}