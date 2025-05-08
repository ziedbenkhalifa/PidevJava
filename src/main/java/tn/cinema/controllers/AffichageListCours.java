package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Cour;
import tn.cinema.services.CourService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AffichageListCours implements Initializable {

    @FXML
    private ListView<Cour> rlist;

    @FXML
    private Button retourButton;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    private TextField searchField;

    @FXML
    private ImageView searchIcon;

    @FXML
    private ComboBox<String> typeFilterComboBox;

    private final CourService courService = new CourService();
    private List<Cour> allCours; // Liste complète des cours pour le filtrage et la recherche

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Récupérer tous les cours
            allCours = courService.recuperer();

            // Trier automatiquement par coût croissant
            allCours.sort(Comparator.comparingDouble(Cour::getCout));

            // Afficher les cours triés
            setRlistItems(allCours);

            // Configurer la ListView avec une cellule personnalisée
            rlist.setCellFactory(listView -> new CustomCourCell());
            rlist.setStyle("-fx-background-color: #192342; -fx-control-inner-background: #192342;");

            // Désactiver les boutons Modifier et Supprimer par défaut
            btnModifier.setDisable(true);
            btnSupprimer.setDisable(true);

            // Activer les boutons lorsque l'utilisateur sélectionne un cours
            rlist.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                btnModifier.setDisable(newSelection == null);
                btnSupprimer.setDisable(newSelection == null);
            });

            // Configurer la ComboBox (elle est déjà définie dans le FXML, mais on peut s'assurer qu'elle est initialisée)
            typeFilterComboBox.setValue("Tous");

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de la récupération des cours : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void setRlistItems(List<Cour> items) {
        rlist.setItems(FXCollections.observableArrayList(items));
    }

    private class CustomCourCell extends ListCell<Cour> {
        private HBox hbox = new HBox();
        private Text idText = new Text();
        private Text typeCourText = new Text();
        private Text coutText = new Text();
        private Text dateDebutText = new Text();
        private Text dateFinText = new Text();

        public CustomCourCell() {
            super();

            idText.setStyle("-fx-fill: white;");
            typeCourText.setStyle("-fx-fill: white;");
            coutText.setStyle("-fx-fill: white;");
            dateDebutText.setStyle("-fx-fill: white;");
            dateFinText.setStyle("-fx-fill: white;");

            idText.setWrappingWidth(50);
            typeCourText.setWrappingWidth(120);
            coutText.setWrappingWidth(80);
            dateDebutText.setWrappingWidth(150);
            dateFinText.setWrappingWidth(150);

            hbox.setSpacing(20);
            hbox.getChildren().addAll(idText, typeCourText, coutText, dateDebutText, dateFinText);
            hbox.setStyle("-fx-background-color: #2a2f4a; -fx-padding: 10; -fx-background-radius: 5;");
            setPrefHeight(40);
        }

        @Override
        protected void updateItem(Cour cour, boolean empty) {
            super.updateItem(cour, empty);
            if (empty || cour == null) {
                setGraphic(null);
                setText(null);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                idText.setText(String.valueOf(cour.getId()));
                typeCourText.setText(cour.getTypeCour());
                coutText.setText(String.format("%.2f", cour.getCout()));
                dateDebutText.setText(cour.getDateDebut().format(formatter));
                dateFinText.setText(cour.getDateFin().format(formatter));

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
    private void applySearchAction(ActionEvent event) {
        String searchText = searchField.getText().trim().toLowerCase();
        List<Cour> filteredCours = allCours;

        // Filtrer par type de cours (si un filtre est appliqué via la ComboBox)
        filteredCours = applyTypeFilter(filteredCours);

        // Appliquer la recherche par typeCour ou cout
        if (!searchText.isEmpty()) {
            try {
                // Essayer de convertir le texte de recherche en double pour chercher par coût
                double searchCout = Double.parseDouble(searchText);
                filteredCours = filteredCours.stream()
                        .filter(cour -> cour.getTypeCour().toLowerCase().contains(searchText) || cour.getCout() == searchCout)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Si la conversion échoue, chercher uniquement par typeCour
                filteredCours = filteredCours.stream()
                        .filter(cour -> cour.getTypeCour().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());
            }
        }

        // Trier les résultats par coût croissant
        filteredCours.sort(Comparator.comparingDouble(Cour::getCout));

        // Mettre à jour la ListView
        setRlistItems(filteredCours);
    }

    @FXML
    private void filterByTypeAction(ActionEvent event) {
        List<Cour> filteredCours = allCours;

        // Appliquer le filtre par type de cours
        filteredCours = applyTypeFilter(filteredCours);

        // Appliquer la recherche si le champ de recherche n'est pas vide
        String searchText = searchField.getText().trim().toLowerCase();
        if (!searchText.isEmpty()) {
            try {
                double searchCout = Double.parseDouble(searchText);
                filteredCours = filteredCours.stream()
                        .filter(cour -> cour.getTypeCour().toLowerCase().contains(searchText) || cour.getCout() == searchCout)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                filteredCours = filteredCours.stream()
                        .filter(cour -> cour.getTypeCour().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());
            }
        }


        filteredCours.sort(Comparator.comparingDouble(Cour::getCout));


        setRlistItems(filteredCours);
    }

    private List<Cour> applyTypeFilter(List<Cour> cours) {
        String selectedType = typeFilterComboBox.getValue();
        if (selectedType == null || "Tous".equals(selectedType)) {
            return cours;
        }
        return cours.stream()
                .filter(cour -> cour.getTypeCour().equalsIgnoreCase(selectedType))
                .collect(Collectors.toList());
    }

    @FXML
    private void modifierAction() {
        Cour selectedCour = rlist.getSelectionModel().getSelectedItem();
        if (selectedCour != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCour.fxml"));
                Parent root = loader.load();

                ModifierCour controller = loader.getController();
                controller.setCour(selectedCour);

                Stage stage = (Stage) rlist.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors du chargement de la fenêtre de modification : " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void supprimerAction() {
        Cour selectedCour = rlist.getSelectionModel().getSelectedItem();
        if (selectedCour != null) {
            try {
                courService.supprimer(selectedCour.getId());
                allCours = courService.recuperer();
                allCours.sort(Comparator.comparingDouble(Cour::getCout)); // Retrier après suppression
                setRlistItems(allCours);
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
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterCour.fxml"));
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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