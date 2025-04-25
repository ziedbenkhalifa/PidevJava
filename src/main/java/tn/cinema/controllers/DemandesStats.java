package tn.cinema.controllers;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class DemandesStats extends Dashboard implements Initializable {

    @FXML private PieChart statusPieChart;
    @FXML private PieChart typePieChart;
    @FXML private BarChart<String, Number> clientBarChart;
    @FXML private TableView<ClientProfitability> profitabilityTable;
    @FXML private TableColumn<ClientProfitability, String> emailColumn;
    @FXML private TableColumn<ClientProfitability, Integer> daysColumn;
    @FXML private TableColumn<ClientProfitability, Double> profitabilityColumn;
    @FXML private VBox statsContainer;

    private final DemandeService demandeService = new DemandeService();

    public static class ClientProfitability {
        private final String email;
        private final int totalDays;
        private final double profitability;

        public ClientProfitability(String email, int totalDays, double profitability) {
            this.email = email;
            this.totalDays = totalDays;
            this.profitability = profitability;
        }

        public String getEmail() { return email; }
        public int getTotalDays() { return totalDays; }
        public double getProfitability() { return profitability; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupCharts();
        loadData();
        applyFadeAnimation();
    }

    private void setupTableColumns() {
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        daysColumn.setCellValueFactory(new PropertyValueFactory<>("totalDays"));
        profitabilityColumn.setCellValueFactory(new PropertyValueFactory<>("profitability"));

        // Style des cellules
        emailColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-background-color: #413052; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
            }
        });

        daysColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item == null ? "" : item.toString());
                setStyle("-fx-background-color: #413052; -fx-text-fill: white; -fx-alignment: CENTER;");
            }
        });

        profitabilityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f TND", item));
                }
                setStyle("-fx-background-color: #413052; -fx-text-fill: white; -fx-alignment: CENTER_RIGHT;");
            }
        });

        // Style du tableau
        profitabilityTable.setStyle("-fx-background-color: #413052;");
        profitabilityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Style des lignes
        profitabilityTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ClientProfitability item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("-fx-background-color: #413052;");
                } else {
                    setStyle("-fx-background-color: #413052; -fx-text-fill: white; -fx-border-color: #5d4a6b;");
                }
            }
        });
    }

    private void setupCharts() {
        statusPieChart.setLegendVisible(true);
        typePieChart.setLegendVisible(true);
        clientBarChart.setLegendVisible(false);

        String chartStyle = "-fx-text-fill: white;";
        statusPieChart.setStyle(chartStyle);
        typePieChart.setStyle(chartStyle);
        clientBarChart.setStyle(chartStyle);
    }

    private void loadData() {
        try {
            List<Demande> demandes = demandeService.recuperer();
            loadStatusData(demandes);
            loadTypeData(demandes);
            loadClientData(demandes);
            loadProfitabilityData(demandes);

            // Appliquer le style des en-têtes après le chargement des données
            Platform.runLater(this::styleTableHeaders);
        } catch (SQLException e) {
            showErrorAlert("Erreur de chargement", e.getMessage());
        }
    }

    private void styleTableHeaders() {
        try {
            Region header = (Region) profitabilityTable.lookup("TableHeaderRow");
            if (header != null) {
                header.setStyle("-fx-background-color: #413052;");

                for (Node child : header.getChildrenUnmodifiable()) {
                    if (child instanceof Label) {
                        ((Label) child).setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error styling table headers: " + e.getMessage());
        }
    }

    private void loadStatusData(List<Demande> demandes) {
        Map<String, Long> statusCounts = demandes.stream()
                .collect(Collectors.groupingBy(
                        d -> capitalize(d.getStatut()),
                        Collectors.counting()));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        statusCounts.forEach((status, count) ->
                pieData.add(new PieChart.Data(status + " (" + count + ")", count))
        );

        statusPieChart.setData(pieData);
    }

    private void loadTypeData(List<Demande> demandes) {
        Map<String, Long> typeCounts = demandes.stream()
                .collect(Collectors.groupingBy(
                        d -> capitalize(d.getType()),
                        Collectors.counting()));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        typeCounts.forEach((type, count) ->
                pieData.add(new PieChart.Data(type + " (" + count + ")", count))
        );

        typePieChart.setData(pieData);
    }

    private void loadClientData(List<Demande> demandes) {
        Map<String, Long> clientCounts = demandes.stream()
                .collect(Collectors.groupingBy(
                        Demande::getEmail,
                        Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        clientCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> series.getData().add(
                        new XYChart.Data<>(shortenEmail(entry.getKey()), entry.getValue())));

        clientBarChart.getData().add(series);
    }

    private void loadProfitabilityData(List<Demande> demandes) {
        Map<String, List<Demande>> byClient = demandes.stream()
                .collect(Collectors.groupingBy(Demande::getEmail));

        ObservableList<ClientProfitability> data = FXCollections.observableArrayList();
        byClient.forEach((email, clientDemandes) -> {
            int days = clientDemandes.stream().mapToInt(Demande::getNombreJours).sum();
            double profit = calculateProfit(clientDemandes);
            data.add(new ClientProfitability(email, days, profit));
        });

        data.sort(Comparator.comparingDouble(ClientProfitability::getProfitability).reversed());
        profitabilityTable.setItems(data);
    }

    private double calculateProfit(List<Demande> demandes) {
        return demandes.stream().mapToDouble(d -> {
            double rate = "approuvee".equalsIgnoreCase(d.getStatut()) ? 100.0 : 50.0;
            return d.getNombreJours() * rate;
        }).sum();
    }

    private void applyFadeAnimation() {
        FadeTransition ft = new FadeTransition(Duration.millis(800), statsContainer);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase()
                .replace("_", " ");
    }

    private String shortenEmail(String email) {
        if (email == null) return "";
        int atIndex = email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}