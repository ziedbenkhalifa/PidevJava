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
import tn.cinema.entities.Seance;
import tn.cinema.services.SeanceService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherSeance implements Initializable {

    @FXML
    private TextField rdateSeance;

    @FXML
    private TextField rduree;

    @FXML
    private TextField robjectifs;

    @FXML
    private TextField rcourId;

    @FXML
    private ListView<Seance> rlist;

    @FXML
    private Button retourButton;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    private final SeanceService seanceService = new SeanceService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadSeances();


        rlist.setCellFactory(listView -> new CustomSeanceCell());


        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);


        rlist.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnModifier.setDisable(newSelection == null);
            btnSupprimer.setDisable(newSelection == null);

            if (newSelection != null) {
                setRdateSeance(newSelection.getDateSeance());
                setRduree(newSelection.getDuree());
                setRobjectifs(newSelection.getObjectifs());
                setRcourId(newSelection.getCour() != null ? newSelection.getCour().getId() : 0);
            } else {
                rdateSeance.clear();
                rduree.clear();
                robjectifs.clear();
                rcourId.clear();
            }
        });
    }

    private void loadSeances() {
        try {
            List<Seance> seances = seanceService.recuperer();
            rlist.setItems(FXCollections.observableArrayList(seances));
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du chargement des séances : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void setRdateSeance(LocalDate dateSeance) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        rdateSeance.setText(dateSeance.format(formatter));
    }

    public void setRduree(LocalTime duree) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        rduree.setText(duree.format(formatter));
    }

    public void setRobjectifs(String objectifs) {
        robjectifs.setText(objectifs);
    }

    public void setRcourId(int courId) {
        rcourId.setText(String.valueOf(courId));
    }

    public void setRlistItems(List<Seance> items) {
        rlist.setItems(FXCollections.observableArrayList(items));
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
                controller.setSeanceToModify(selectedSeance); // Updated method name

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
                seanceService.supprimer(selectedSeance.getId());
                List<Seance> updatedSeances = seanceService.recuperer();
                setRlistItems(updatedSeances);
                btnModifier.setDisable(true);
                btnSupprimer.setDisable(true);
            } catch (SQLException e) {
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
            Parent root = FXMLLoader.load(getClass().getResource("/GestionSeance.fxml"));
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