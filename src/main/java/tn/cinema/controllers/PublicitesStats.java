package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import tn.cinema.entities.Publicite;
import tn.cinema.services.PubliciteService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PublicitesStats extends Dashboard implements Initializable {
    @FXML private VBox statsContainer;
    @FXML private Button prevMonthButton;
    @FXML private Button nextMonthButton;
    @FXML private Button todayButton;
    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private TableView<Map.Entry<Integer, Double>> yearlyRevenueTable;
    @FXML private TableColumn<Map.Entry<Integer, Double>, String> yearColumn;
    @FXML private TableColumn<Map.Entry<Integer, Double>, String> revenueColumn;
    @FXML private LineChart<Number, Number> revenueChart;

    private PubliciteService publiciteService = new PubliciteService();
    private ObservableList<Publicite> publicites;
    private List<String> months = Arrays.asList("Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre");
    private LocalDate currentDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentDate = LocalDate.now();
        updateMonthYearLabel();

        // Style the table
        yearlyRevenueTable.setStyle("-fx-background-color: #0d1a40;");

        // Style the columns headers to match the content
        yearColumn.setStyle("-fx-background-color: #1a2b5f; -fx-text-fill: white; -fx-alignment: CENTER;");
        revenueColumn.setStyle("-fx-background-color: #1a2b5f; -fx-text-fill: white; -fx-alignment: CENTER;");

        // Apply style to column headers
        yearlyRevenueTable.lookupAll(".column-header").forEach(node ->
                node.setStyle("-fx-background-color: #1a2b5f; -fx-text-fill: white;")
        );

        // Style the chart
        setupChartStyle();

        // Load data
        loadPublicites();
        updateCalendar();
        loadYearlyRevenue();
        updateRevenueChart();

        // Set up button actions
        prevMonthButton.setOnAction(event -> {
            currentDate = currentDate.minusMonths(1);
            updateMonthYearLabel();
            updateCalendar();
        });

        nextMonthButton.setOnAction(event -> {
            currentDate = currentDate.plusMonths(1);
            updateMonthYearLabel();
            updateCalendar();
        });

        todayButton.setOnAction(event -> {
            currentDate = LocalDate.now();
            updateMonthYearLabel();
            updateCalendar();
        });
    }

    private void setupChartStyle() {
        // Set chart background and text colors
        revenueChart.setStyle("-fx-background-color: transparent;");
        revenueChart.setLegendVisible(false);

        // Style axes
        NumberAxis xAxis = (NumberAxis) revenueChart.getXAxis();
        xAxis.setTickLabelFill(Color.WHITE);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setStyle("-fx-text-fill: white;");
        xAxis.setForceZeroInRange(false);
        xAxis.setTickUnit(1);
        xAxis.setMinorTickCount(0);
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%d", object.intValue());
            }
        });

        NumberAxis yAxis = (NumberAxis) revenueChart.getYAxis();
        yAxis.setTickLabelFill(Color.WHITE);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setStyle("-fx-text-fill: white;");

        // Style chart title
        revenueChart.lookup(".chart-title").setStyle("-fx-text-fill: white; -fx-font-size: 16;");
    }

    private void updateMonthYearLabel() {
        String monthName = months.get(currentDate.getMonthValue() - 1);
        monthYearLabel.setText(monthName + " " + currentDate.getYear());
    }

    private void loadPublicites() {
        try {
            publicites = FXCollections.observableArrayList(publiciteService.recupererpub());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des publicités : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateCalendar() {
        // Clear previous calendar entries (except headers)
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

        YearMonth yearMonth = YearMonth.of(currentDate.getYear(), currentDate.getMonthValue());
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = yearMonth.lengthOfMonth();

        // Map to store advertisements per day
        Map<Integer, List<Publicite>> adsByDay = publicites.stream()
                .filter(pub -> isPubInCurrentMonth(pub, yearMonth))
                .collect(Collectors.groupingBy(pub -> {
                    LocalDate startDate = pub.getDateDebut().toLocalDate();
                    LocalDate endDate = pub.getDateFin().toLocalDate();
                    List<Integer> days = new ArrayList<>();

                    for (int day = 1; day <= daysInMonth; day++) {
                        LocalDate date = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), day);
                        if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                            days.add(day);
                        }
                    }
                    return days;
                }))
                .entrySet().stream()
                .flatMap(entry -> entry.getKey().stream().map(day -> Map.entry(day, entry.getValue())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.flatMapping(e -> e.getValue().stream(), Collectors.toList())
                ));

        // Populate calendar
        for (int day = 1; day <= daysInMonth; day++) {
            int row = (day + dayOfWeek - 1) / 7 + 1;
            int col = (day + dayOfWeek - 1) % 7;

            VBox dayBox = new VBox(2);
            dayBox.setPrefSize(140, 60);
            dayBox.setStyle("-fx-alignment: center; -fx-padding: 5; -fx-background-radius: 5;");

            Text dayText = new Text(String.valueOf(day));
            dayText.setStyle("-fx-fill: white; -fx-font-size: 14;");

            // Highlight today
            if (LocalDate.now().equals(LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), day))) {
                dayBox.setStyle("-fx-background-color: #FFD700; -fx-alignment: center; -fx-padding: 5; -fx-background-radius: 5;");
                dayText.setStyle("-fx-fill: black; -fx-font-weight: bold; -fx-font-size: 14;");
            }

            // Highlight days with ads
            if (adsByDay.containsKey(day)) {
                dayBox.setStyle("-fx-background-color: #4CAF50; -fx-alignment: center; -fx-padding: 5; -fx-background-radius: 5;");

                // Create tooltip with ad details
                Tooltip tooltip = new Tooltip();
                StringBuilder tooltipText = new StringBuilder("Publicités ce jour:\n\n");
                adsByDay.get(day).forEach(pub -> {
                    tooltipText.append("• ").append(pub.getSupport())
                            .append(" (ID: ").append(pub.getId()).append(")\n")
                            .append("Montant: ").append(String.format("%.2f TND", pub.getMontant()))
                            .append("\nDu ").append(formatDate(pub.getDateDebut().toLocalDate()))
                            .append(" au ").append(formatDate(pub.getDateFin().toLocalDate()))
                            .append("\n\n");
                });
                tooltip.setText(tooltipText.toString());
                Tooltip.install(dayBox, tooltip);
            }

            dayBox.getChildren().add(dayText);
            calendarGrid.add(dayBox, col, row);
        }
    }

    private boolean isPubInCurrentMonth(Publicite pub, YearMonth yearMonth) {
        LocalDate startDate = pub.getDateDebut().toLocalDate();
        LocalDate endDate = pub.getDateFin().toLocalDate();

        return !(endDate.isBefore(yearMonth.atDay(1)) || startDate.isAfter(yearMonth.atEndOfMonth()));
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private void loadYearlyRevenue() {
        Map<Integer, Double> revenueByYear = calculateYearlyRevenue();

        ObservableList<Map.Entry<Integer, Double>> revenueData = FXCollections.observableArrayList(revenueByYear.entrySet());
        revenueData.sort(Map.Entry.comparingByKey());

        yearColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKey().toString()));
        revenueColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f TND", cellData.getValue().getValue())));

        yearlyRevenueTable.setItems(revenueData);

        // Style table rows
        yearlyRevenueTable.setRowFactory(tv -> new TableRow<Map.Entry<Integer, Double>>() {
            @Override
            protected void updateItem(Map.Entry<Integer, Double> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    setStyle("-fx-background-color: #1a2b5f; -fx-text-fill: white;");
                }
            }
        });
    }

    private void updateRevenueChart() {
        Map<Integer, Double> revenueByYear = calculateYearlyRevenue();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Revenus Publicitaires");

        // Add data to the series
        revenueByYear.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    XYChart.Data<Number, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
                    series.getData().add(data);
                });

        // Clear previous data and add the new series
        revenueChart.getData().clear();
        revenueChart.getData().add(series);

        // Dynamically set the X-axis bounds based on the data
        NumberAxis xAxis = (NumberAxis) revenueChart.getXAxis();
        if (!revenueByYear.isEmpty()) {
            int minYear = revenueByYear.keySet().stream().min(Integer::compareTo).orElse(LocalDate.now().getYear());
            int maxYear = revenueByYear.keySet().stream().max(Integer::compareTo).orElse(LocalDate.now().getYear());
            xAxis.setLowerBound(minYear - 1);
            xAxis.setUpperBound(maxYear + 1);
        } else {
            int currentYear = LocalDate.now().getYear();
            xAxis.setLowerBound(currentYear - 1);
            xAxis.setUpperBound(currentYear + 1);
        }

        // Style the series
        series.getNode().setStyle("-fx-stroke: #4CAF50; -fx-stroke-width: 2px;");

        for (XYChart.Data<Number, Number> data : series.getData()) {
            Node node = data.getNode();
            if (node != null) {
                node.setStyle("-fx-background-color: #4CAF50, white;");

                // Add data labels
                Tooltip tooltip = new Tooltip(String.format("Année: %d\nRevenus: %.2f TND",
                        data.getXValue().intValue(), data.getYValue().doubleValue()));
                Tooltip.install(node, tooltip);
            }
        }
    }

    private Map<Integer, Double> calculateYearlyRevenue() {
        Map<Integer, Double> revenueByYear = new TreeMap<>();

        if (publicites != null) {
            for (Publicite pub : publicites) {
                LocalDate startDate = pub.getDateDebut().toLocalDate();
                int year = startDate.getYear();
                double revenue = pub.getMontant();
                revenueByYear.merge(year, revenue, Double::sum);
            }
        }
        return revenueByYear;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}