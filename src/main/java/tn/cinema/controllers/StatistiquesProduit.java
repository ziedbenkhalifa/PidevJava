package tn.cinema.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.cinema.services.ProduitService;
import tn.cinema.entities.Produit;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class StatistiquesProduit implements Initializable {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private PieChart pieChart; // Ajout du PieChart

    private final ProduitService produitService = new ProduitService();
    @FXML
    private ListView<HBox> listViewProduits;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadChartData();
        loadPieChartData(); // Méthode pour charger les données du PieChart
        applyAnimation();
    }

    private void loadChartData() {
        Map<String, Integer> stats = produitService.getNombreProduitsParCategorie();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Produits par Catégorie");

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
    }

    private void loadPieChartData() {
        pieChart.getData().clear(); // Nettoyer les anciennes données

        // Produit le plus cher
        Produit produitLePlusCher = produitService.produitLePlusCher();
        if (produitLePlusCher != null) {
            PieChart.Data slice = new PieChart.Data(
                    produitLePlusCher.getNom() + " — Plus cher (" + produitLePlusCher.getPrix() + " TND)",
                    produitLePlusCher.getPrix()
            );
            pieChart.getData().add(slice);
        } else {
            System.err.println("❌ Aucun produit le plus cher trouvé.");
        }

        // Produit le moins cher
        Produit produitLeMoinsCher = produitService.produitLeMoinsCher();
        if (produitLeMoinsCher != null) {
            PieChart.Data slice = new PieChart.Data(
                    produitLeMoinsCher.getNom() + " — Moins cher (" + produitLeMoinsCher.getPrix() + " TND)",
                    produitLeMoinsCher.getPrix()
            );
            pieChart.getData().add(slice);
        } else {
            System.err.println("❌ Aucun produit le moins cher trouvé.");
        }

        // Prix moyen
        double prixMoyen = produitService.prixMoyenProduits();
        if (prixMoyen > 0) {
            PieChart.Data moyenneSlice = new PieChart.Data(
                    "Prix moyen (" + prixMoyen + " TND)",
                    prixMoyen
            );
            pieChart.getData().add(moyenneSlice);
        } else {
            System.err.println("⚠️ Impossible de calculer le prix moyen (peut-être aucun produit).");
        }
    }

    private void applyAnimation() {
        // Animation générale pour le BarChart
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), barChart);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), barChart);
        scaleTransition.setFromX(0.8);
        scaleTransition.setToX(1);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToY(1);
        scaleTransition.play();

        animateBars();

        // Animation générale pour le PieChart
        FadeTransition fadePieTransition = new FadeTransition(Duration.seconds(1), pieChart);
        fadePieTransition.setFromValue(0);
        fadePieTransition.setToValue(1);
        fadePieTransition.play();

        ScaleTransition scalePieTransition = new ScaleTransition(Duration.seconds(1), pieChart);
        scalePieTransition.setFromX(0.8);
        scalePieTransition.setToX(1);
        scalePieTransition.setFromY(0.8);
        scalePieTransition.setToY(1);
        scalePieTransition.play();
    }

    private void animateBars() {
        // Animation des barres du BarChart
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node bar = data.getNode();

                FadeTransition barFade = new FadeTransition(Duration.seconds(1), bar);
                barFade.setFromValue(0);
                barFade.setToValue(1);
                barFade.play();

                ScaleTransition barScale = new ScaleTransition(Duration.seconds(1), bar);
                barScale.setFromX(0.8);
                barScale.setToX(1);
                barScale.setFromY(0.8);
                barScale.setToY(1);
                barScale.play();
            }
        }
    }
    @FXML
    void afficher(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la page des produits.");
        }
    }
    @FXML
    void back(ActionEvent event) {
        try {
            // Charger la scène FXML qui affiche la liste des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            // Obtenez la scène actuelle et changez son contenu (root)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root); // Remplacer le contenu de la scène actuelle par root

        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la page de dashboard.");
        }
    }
    private void afficherAlerte(String erreur, String s) {
    }
}