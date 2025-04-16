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
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Cour;
import tn.cinema.entities.Seance;
import tn.cinema.services.SeanceService;
import tn.cinema.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
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

    private SeanceService seanceService = new SeanceService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger les séances depuis la base de données
        loadSeances();

        // Définir une cellule personnalisée pour afficher les séances
        rlist.setCellFactory(listView -> new CustomSeanceCell());

        // Appliquer un style à la ListView
        rlist.setStyle("-fx-background-color: #192342; -fx-control-inner-background: #192342;");

        // Désactiver les boutons Modifier et Supprimer par défaut
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);

        // Activer les boutons Modifier et Supprimer lorsqu'une séance est sélectionnée
        rlist.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnModifier.setDisable(newSelection == null);
            btnSupprimer.setDisable(newSelection == null);
        });
    }

    private void loadSeances() {
        try {
            // Charger les séances depuis la base de données
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

    private class CustomSeanceCell extends ListCell<Seance> {
        private HBox hbox = new HBox();
        private Text idText = new Text();
        private Text dateSeanceText = new Text();
        private Text dureeText = new Text();
        private Text objectifsText = new Text();
        private Text courText = new Text();

        public CustomSeanceCell() {
            super();

            idText.setStyle("-fx-fill: white;");
            dateSeanceText.setStyle("-fx-fill: white;");
            dureeText.setStyle("-fx-fill: white;");
            objectifsText.setStyle("-fx-fill: white;");
            courText.setStyle("-fx-fill: white;");

            idText.setWrappingWidth(50);
            dateSeanceText.setWrappingWidth(120);
            dureeText.setWrappingWidth(80);
            objectifsText.setWrappingWidth(150);
            courText.setWrappingWidth(150);

            hbox.setSpacing(20);
            hbox.getChildren().addAll(idText, dateSeanceText, dureeText, objectifsText, courText);
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
                Cour cour = seance.getCour();
                courText.setText(cour != null ? (cour.getTypeCour() != null ? cour.getTypeCour() : "ID: " + cour.getId()) : "N/A");

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
                // Supprimer la séance de la base de données
                seanceService.supprimer(selectedSeance.getId());

                // Recharger les données depuis la base de données
                loadSeances();

                // Désactiver les boutons après la suppression
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


    @FXML
    private Button gestionPubButton;

    @FXML
    private void goToInterfaceChoixGP() {
        try {
            // Load InterfaceChoixGP.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceChoixGP.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the button
            Stage stage = (Stage) gestionPubButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion Publicités");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert to the user
            System.err.println("Error loading InterfaceChoixGP.fxml: " + e.getMessage());
        }
    }

    @FXML
    private Button gererDemandesButton;
    public void goToInterfaceDemandes(ActionEvent actionEvent) {
        try {
            // Load InterfaceDemandes.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceDemandes.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the button
            Stage stage = (Stage) gererDemandesButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Demandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert to the user
            System.err.println("Error loading InterfaceDemandes.fxml: " + e.getMessage());
        }
    }
    @FXML
    private Button gererPublictesButton;
    public void goToInterfacePublictes(ActionEvent actionEvent) {
        try {
            // Load InterfaceDemandes.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfacePublicites.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the button
            Stage stage = (Stage) gererDemandesButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Demandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert to the user
            System.err.println("Error loading InterfaceDemandes.fxml: " + e.getMessage());
        }
    }

    @FXML
    private Button backButton;
    public void goBackToLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Dashboard.fxml: " + e.getMessage());
        }
    }






    /// aliiiiiiiiii


    @FXML
    private Button gestionUserButton;

    @FXML
    private Button monCompteButton;

    @FXML
    private void handleGestionUserAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gestionUserButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger AfficherUser.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleMonCompteAction() {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            showAlert("Erreur", "Aucun utilisateur connecté.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MonCompte.fxml"));
            Parent root = loader.load();
            MonCompte controller = loader.getController();
            controller.setLoggedInUser(SessionManager.getInstance().getLoggedInUser());
            Stage stage = (Stage) monCompteButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Compte");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger MonCompte.fxml: " + e.getMessage());
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    void gestionFilm(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionFilms.fxml")); // Adjust path if needed
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();   // Get current window
            Scene scene = new Scene(root);
            stage.setScene(scene); // Set the new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // For debug
        }
    }

    @FXML
    private Button btnAjouterEquipement;
    @FXML
    private void ouvrirInterfaceAjout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ListeSalle.fxml"));
            Parent root = loader.load();
            btnAjouterEquipement.getScene().setRoot(root); // Remplacer la scène actuelle
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur de chargement");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Impossible d'ouvrir l'interface Ajouter Équipement.");
            errorAlert.showAndWait();
        }
    }



    @FXML
    void affichage(ActionEvent event) {
        try {
            // Charger la scène FXML qui affiche la liste des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChoixG.fxml"));
            Parent root = loader.load();

            // Obtenez la scène actuelle et changez son contenu (root)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root); // Remplacer le contenu de la scène actuelle par root

        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la page des produits.");
        }
    }

    // Méthode pour afficher une alerte
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleGestinCour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GestionCour.fxml"));
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}