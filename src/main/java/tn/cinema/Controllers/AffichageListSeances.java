package tn.cinema.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Cour;
import tn.cinema.entities.Seance;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AffichageListSeances implements Initializable {

    @FXML
    private ListView<Seance> rlist;

    @FXML
    private Button retourButton;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load Seances into the ListView
        loadSeances();

        // Set the custom cell factory
        rlist.setCellFactory(listView -> new CustomSeanceCell());

        // Ensure the ListView cells are always visible by adjusting the style
        rlist.setStyle("-fx-background-color: #192342; -fx-control-inner-background: #192342;");

        // Disable Modifier and Supprimer buttons until a row is selected
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);

        // Enable buttons when a row is selected
        rlist.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnModifier.setDisable(newSelection == null);
            btnSupprimer.setDisable(newSelection == null);
        });
    }

    private void loadSeances() {
        // Manually create the Seance objects as per the image
        List<Seance> seances = new ArrayList<>();

        // Seance 1: ID 15
        Cour cour1 = new Cour();
        cour1.setId(29);
        Seance seance1 = new Seance();
        seance1.setId(15);
        seance1.setDateSeance(LocalDate.parse("2025-03-04"));
        seance1.setDuree(LocalTime.parse("01:30"));
        seance1.setObjectifs("Compréhension des émotions");
        seance1.setCour(cour1);
        seances.add(seance1);

        // Seance 2: ID 18
        Cour cour2 = new Cour();
        cour2.setId(30);
        Seance seance2 = new Seance();
        seance2.setId(18);
        seance2.setDateSeance(LocalDate.parse("2025-04-05"));
        seance2.setDuree(LocalTime.parse("02:00"));
        seance2.setObjectifs("Développement des compétences techniques");
        seance2.setCour(cour2);
        seances.add(seance2);

        // Seance 3: ID 19
        Cour cour3 = new Cour();
        cour3.setId(38);
        Seance seance3 = new Seance();
        seance3.setId(19);
        seance3.setDateSeance(LocalDate.parse("2025-04-09"));
        seance3.setDuree(LocalTime.parse("03:30"));
        seance3.setObjectifs("Histoire du Cinéma");
        seance3.setCour(cour3);
        seances.add(seance3);

        // Seance 4: ID 33
        Cour cour4 = new Cour();
        cour4.setId(43);
        Seance seance4 = new Seance();
        seance4.setId(33);
        seance4.setDateSeance(LocalDate.parse("2025-04-26"));
        seance4.setDuree(LocalTime.parse("01:00"));
        seance4.setObjectifs("dkjbdkjcnkjcndklcnkdlklcndlc,klc,dlkcljdclkjdclk");
        seance4.setCour(cour4);
        seances.add(seance4);

        // Seance 5: ID 34
        Cour cour5 = new Cour();
        cour5.setId(176);
        Seance seance5 = new Seance();
        seance5.setId(34);
        seance5.setDateSeance(LocalDate.parse("2025-04-03"));
        seance5.setDuree(LocalTime.parse("01:00"));
        seance5.setObjectifs("djcbjdkcnkcleklcdc");
        seance5.setCour(cour5);
        seances.add(seance5);

        // Seance 6: ID 36
        Cour cour6 = new Cour();
        cour6.setId(32);
        Seance seance6 = new Seance();
        seance6.setId(36);
        seance6.setDateSeance(LocalDate.parse("2025-05-01"));
        seance6.setDuree(LocalTime.parse("14:00"));
        seance6.setObjectifs("fjvkfnvfknd");
        seance6.setCour(cour6);
        seances.add(seance6);

        rlist.setItems(FXCollections.observableArrayList(seances));
    }

    private class CustomSeanceCell extends ListCell<Seance> {
        private HBox hbox = new HBox();
        private Text idText = new Text();
        private Text dateSeanceText = new Text();
        private Text dureeText = new Text();
        private Text objectifsText = new Text();
        private Text courIdText = new Text();

        public CustomSeanceCell() {
            super();

            idText.setStyle("-fx-fill: white;");
            dateSeanceText.setStyle("-fx-fill: white;");
            dureeText.setStyle("-fx-fill: white;");
            objectifsText.setStyle("-fx-fill: white;");
            courIdText.setStyle("-fx-fill: white;");

            idText.setWrappingWidth(50);
            dateSeanceText.setWrappingWidth(120);
            dureeText.setWrappingWidth(80);
            objectifsText.setWrappingWidth(150);
            courIdText.setWrappingWidth(80);

            hbox.setSpacing(20);
            hbox.getChildren().addAll(idText, dateSeanceText, dureeText, objectifsText, courIdText);
            hbox.setStyle("-fx-background-color: #2a2f4a; -fx-padding: 10; -fx-background-radius: 5;");
            setPrefHeight(40);
        }

        @Override
        protected void updateItem(Seance seance, boolean empty) {
            super.updateItem(seance, empty);
            if (empty || seance == null) {
                setGraphic(null);
                setText(null);
            } else {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                idText.setText(String.valueOf(seance.getId()));
                dateSeanceText.setText(seance.getDateSeance().format(dateFormatter));
                dureeText.setText(seance.getDuree().format(timeFormatter));
                objectifsText.setText(seance.getObjectifs());
                courIdText.setText(String.valueOf(seance.getCour() != null ? seance.getCour().getId() : "N/A"));

                setGraphic(hbox);
                setStyle("-fx-background-color: transparent;");

                if (isSelected()) {
                    hbox.setStyle("-fx-background-color: #3e2063; -fx-padding: 10; -fx-background-radius: 5;");
                } else {
                    hbox.setStyle("-fx-background-color: #2a2f4a; -fx-padding: 10; -fx-background-radius: 5;");
                }
            }
        }
    }

    @FXML
    private void modifierAction() {
        Seance selectedSeance = rlist.getSelectionModel().getSelectedItem();
        if (selectedSeance != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierSeance.fxml"));
                Parent root = loader.load();

                ModifierSeance controller = loader.getController();
                controller.setSeanceToModify(selectedSeance);

                Stage stage = (Stage) rlist.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors du chargement de la fenêtre de modification : " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void supprimerAction() {
        Seance selectedSeance = rlist.getSelectionModel().getSelectedItem();
        if (selectedSeance != null) {
            try {
                // Since we're using static data, just remove from the ListView
                rlist.getItems().remove(selectedSeance);
                btnModifier.setDisable(true);
                btnSupprimer.setDisable(true);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors de la suppression : " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void retourAction() {
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

    @FXML
    private void ajouterAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterSeance.fxml"));
            Stage stage = (Stage) rlist.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'ajout : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleGestionCour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionCour.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) rlist.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de la navigation : " + e.getMessage());
            alert.showAndWait();
        }
    }
}