package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.util.*;
import java.util.stream.Collectors;

public class PublicitesStats extends Dashboard implements Initializable {
    @FXML
    private VBox statsContainer;

    @FXML
    private Button prevMonthButton;

    @FXML
    private Button nextMonthButton;

    @FXML
    private Button todayButton;

    @FXML
    private Label monthYearLabel;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private TableView<Map.Entry<Integer, Double>> yearlyRevenueTable;

    @FXML
    private TableColumn<Map.Entry<Integer, Double>, String> yearColumn;

    @FXML
    private TableColumn<Map.Entry<Integer, Double>, String> revenueColumn;

    private PubliciteService publiciteService = new PubliciteService();
    private ObservableList<Publicite> publicites;
    private List<String> months = Arrays.asList("janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre");
    private LocalDate currentDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set initial date to today
        currentDate = LocalDate.now();
        updateMonthYearLabel();

        // Load data
        loadPublicites();
        updateCalendar();
        loadYearlyRevenue();

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

    private void updateMonthYearLabel() {
        String monthName = months.get(currentDate.getMonthValue() - 1);
        monthYearLabel.setText(monthName + " " + currentDate.getYear());
    }

    private void loadPublicites() {
        try {
            publicites = FXCollections.observableArrayList(publiciteService.recupererpub());
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des publicités : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateCalendar() {
        // Clear previous calendar entries (except headers)
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

        YearMonth yearMonth = YearMonth.of(currentDate.getYear(), currentDate.getMonthValue());
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Adjust for Sunday start (0 = Sunday)
        int daysInMonth = yearMonth.lengthOfMonth();

        // Map to store advertisements per day
        Map<Integer, List<Publicite>> adsByDay = new HashMap<>();
        if (publicites != null) {
            for (Publicite pub : publicites) {
                LocalDate startDate = pub.getDateDebut().toLocalDate();
                LocalDate endDate = pub.getDateFin().toLocalDate();
                if (startDate.getYear() <= currentDate.getYear() && endDate.getYear() >= currentDate.getYear() &&
                        startDate.getMonthValue() <= currentDate.getMonthValue() && endDate.getMonthValue() >= currentDate.getMonthValue()) {
                    for (int day = 1; day <= daysInMonth; day++) {
                        LocalDate date = LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), day);
                        if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                            adsByDay.computeIfAbsent(day, k -> new ArrayList<>()).add(pub);
                        }
                    }
                }
            }
        }

        // Populate calendar
        for (int day = 1; day <= daysInMonth; day++) {
            int row = (day + dayOfWeek - 1) / 7 + 1;
            int col = (day + dayOfWeek - 1) % 7;

            VBox dayBox = new VBox(5);
            dayBox.setPrefSize(140, 40); // Approximate size to match the image
            dayBox.setStyle("-fx-alignment: center; -fx-padding: 5;");

            Text dayText = new Text(String.valueOf(day));
            dayText.setStyle("-fx-fill: #b0bec5; -fx-font-size: 14;");

            // Days before the current month (grayed out)
            if (day < 1 || day > daysInMonth) {
                dayText.setStyle("-fx-fill: #666; -fx-font-size: 14;");
            }

            // Highlight days with ads
            if (adsByDay.containsKey(day)) {
                dayBox.setStyle("-fx-background-color: #4CAF50; -fx-alignment: center; -fx-padding: 5;");
                Tooltip tooltip = new Tooltip();
                StringBuilder tooltipText = new StringBuilder("Publicités:\n");
                for (Publicite pub : adsByDay.get(day)) {
                    tooltipText.append("ID: ").append(pub.getId())
                            .append(", Support: ").append(pub.getSupport())
                            .append(", Montant: ").append(String.format("%.2f", pub.getMontant())).append(" TND\n");
                }
                tooltip.setText(tooltipText.toString());
                Tooltip.install(dayBox, tooltip);
            }

            // Highlight today
            if (LocalDate.now().equals(LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), day))) {
                dayBox.setStyle("-fx-background-color: #FFD700; -fx-alignment: center; -fx-padding: 5;");
            }

            dayBox.getChildren().add(dayText);
            calendarGrid.add(dayBox, col, row);
        }
    }

    private void loadYearlyRevenue() {
        Map<Integer, Double> revenueByYear = new HashMap<>();
        if (publicites != null) {
            for (Publicite pub : publicites) {
                LocalDate startDate = pub.getDateDebut().toLocalDate();
                LocalDate endDate = pub.getDateFin().toLocalDate();
                int startYear = startDate.getYear();
                int endYear = endDate.getYear();

                for (int year = startYear; year <= endYear; year++) {
                    LocalDate yearStart = LocalDate.of(year, 1, 1);
                    LocalDate yearEnd = LocalDate.of(year, 12, 31);
                    LocalDate effectiveStart = startDate.isBefore(yearStart) ? yearStart : startDate;
                    LocalDate effectiveEnd = endDate.isAfter(yearEnd) ? yearEnd : endDate;

                    long days = effectiveEnd.toEpochDay() - effectiveStart.toEpochDay() + 1;
                    long totalDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
                    double revenuePortion = (pub.getMontant() * days) / totalDays;

                    revenueByYear.merge(year, revenuePortion, Double::sum);
                }
            }
        }

        ObservableList<Map.Entry<Integer, Double>> revenueData = FXCollections.observableArrayList(revenueByYear.entrySet());
        revenueData.sort(Map.Entry.comparingByKey());

        yearColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKey().toString()));
        revenueColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cellData.getValue().getValue())));

        yearlyRevenueTable.setItems(revenueData);
    }
}