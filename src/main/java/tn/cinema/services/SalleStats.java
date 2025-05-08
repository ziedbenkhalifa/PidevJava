package tn.cinema.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import tn.cinema.entities.Salle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalleStats {

    public static PieChart createEtatSalleChart(List<Salle> salles) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("ouverte", 0);
        stats.put("En maintenance", 0);
        stats.put("Fermée", 0);

        for (Salle salle : salles) {
            String etat = salle.getStatut();
            if (stats.containsKey(etat)) {
                stats.put(etat, stats.get(etat) + 1);
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("ouverte", stats.get("ouverte")),
                new PieChart.Data("En maintenance", stats.get("En maintenance")),
                new PieChart.Data("Fermée", stats.get("Fermée"))
        );

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("État des salles");
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);

        return pieChart;
    }
}