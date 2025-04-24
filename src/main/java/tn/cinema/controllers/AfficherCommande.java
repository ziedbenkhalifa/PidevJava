package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.cinema.entities.Commande;
import tn.cinema.services.CommandeService;
import tn.cinema.utils.SessionManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;


public class AfficherCommande {

    @FXML
    private ListView<HBox> listViewCommandes;

    private ObservableList<Commande> commandes = FXCollections.observableArrayList();
    private final CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        commandes.addAll(commandeService.recuperer()); // Récupérer toutes les commandes
        afficherCommandes();
    }

    private void afficherCommandes() {
        listViewCommandes.getItems().clear();

        for (Commande c : commandes) {
            // Création des labels pour chaque information de la commande
            Label lblId = new Label("Commande ID: " + c.getId());
            Label lblUserId = new Label("Utilisateur ID: " + c.getUserId());
            Label lblMontant = new Label("Montant payé: " + c.getMontantPaye() + " DT");
            Label lblEtat = new Label("État: " + c.getEtat());
            Label lblDate = new Label("Date: " + c.getDateCommande().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            // Style des labels
            lblId.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");
            lblMontant.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px; -fx-font-weight: bold;");
            lblEtat.setStyle("-fx-font-size: 14px; -fx-text-fill: #f39c12;");
            lblDate.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");

            // Création des boutons Modifier et Supprimer
            Button btnModifier = new Button("Modifier");
            Button btnSupprimer = new Button("Supprimer");

            btnModifier.setOnAction(e -> modifierCommande(c, e));
            btnSupprimer.setOnAction(e -> supprimerCommande(c));

            // Style des boutons
            String btnStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 12; -fx-cursor: hand; -fx-background-color: #3498db; -fx-text-fill: white;";
            String btnDeleteStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 12; -fx-cursor: hand; -fx-background-color: #e74c3c; -fx-text-fill: white;";
            btnModifier.setStyle(btnStyle);
            btnSupprimer.setStyle(btnDeleteStyle);

            // Hover Animation for Buttons
            btnModifier.setOnMouseEntered(e -> {
                btnModifier.setStyle(btnStyle + "-fx-background-color: #2980b9;");
                btnModifier.setScaleX(1.1);
                btnModifier.setScaleY(1.1);
            });
            btnModifier.setOnMouseExited(e -> {
                btnModifier.setStyle(btnStyle);
                btnModifier.setScaleX(1);
                btnModifier.setScaleY(1);
            });

            btnSupprimer.setOnMouseEntered(e -> {
                btnSupprimer.setStyle(btnDeleteStyle + "-fx-background-color: #c0392b;");
                btnSupprimer.setScaleX(1.1);
                btnSupprimer.setScaleY(1.1);
            });
            btnSupprimer.setOnMouseExited(e -> {
                btnSupprimer.setStyle(btnDeleteStyle);
                btnSupprimer.setScaleX(1);
                btnSupprimer.setScaleY(1);
            });

            // Press Animation for Buttons
            btnModifier.setOnMousePressed(e -> {
                btnModifier.setStyle(btnStyle + "-fx-background-color: #1f618d;");
            });
            btnModifier.setOnMouseReleased(e -> {
                btnModifier.setStyle(btnStyle);
            });

            btnSupprimer.setOnMousePressed(e -> {
                btnSupprimer.setStyle(btnDeleteStyle + "-fx-background-color: #922b21;");
            });
            btnSupprimer.setOnMouseReleased(e -> {
                btnSupprimer.setStyle(btnDeleteStyle);
            });

            // Créer un VBox pour les informations de la commande et les boutons
            VBox infoBox = new VBox(5, lblId, lblUserId, lblMontant, lblEtat, lblDate);
            infoBox.setSpacing(8);
            infoBox.setPadding(new Insets(10, 0, 0, 0));

            // Créer un HBox pour les boutons Modifier et Supprimer
            HBox btnBox = new HBox(10, btnModifier, btnSupprimer);
            btnBox.setAlignment(Pos.CENTER_LEFT);
            btnBox.setPadding(new Insets(10, 0, 0, 0));

            // Création de la boîte de la commande avec un style personnalisé
            VBox commandeBox = new VBox(10, infoBox, btnBox);
            commandeBox.setStyle(
                    "-fx-padding: 15px;" +
                            "-fx-background-color: #ffffff;" +
                            "-fx-border-radius: 12px;" +
                            "-fx-border-color: #ecf0f1;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);"
            );

            // Ajout de l'élément HBox pour chaque commande
            HBox card = new HBox(commandeBox);
            card.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10px; -fx-margin: 10px;");
            listViewCommandes.getItems().add(card);
        }
    }

    @FXML
    private void modifierCommande(Commande commande, ActionEvent event) {
        try {
            // Charger le fichier FXML pour la fenêtre de modification de commande
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCommande.fxml"));
            Parent root = loader.load();

            // Obtenez le contrôleur de la nouvelle fenêtre (ModifierCommande)
            ModifierCommande controller = loader.getController();

            // Passer l'objet commande à la méthode setCommande du contrôleur
            controller.setCommandeSelectionnee(commande);  // Modifié pour correspondre à la méthode de votre contrôleur

            // Obtenez la scène actuelle et changez son contenu (root)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d’ouvrir la fenêtre de modification.");
        }
    }

    private void supprimerCommande(Commande c) {
        // Création de la boîte de confirmation personnalisée
        VBox vbox = new VBox(15);
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 15; -fx-padding: 20;");
        vbox.setAlignment(Pos.CENTER);

        Label confirmationMessage = new Label("Êtes-vous sûr de vouloir supprimer cette commande ?");
        confirmationMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        Button btnYes = new Button("Oui");
        Button btnNo = new Button("Non");
        btnYes.setStyle("-fx-font-size: 14px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8;");
        btnNo.setStyle("-fx-font-size: 14px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8;");

        btnYes.setOnMouseEntered(e -> btnYes.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;"));
        btnYes.setOnMouseExited(e -> btnYes.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"));
        btnNo.setOnMouseEntered(e -> btnNo.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;"));
        btnNo.setOnMouseExited(e -> btnNo.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;"));

        // Créer une nouvelle scène pour afficher la confirmation
        Stage confirmationStage = new Stage();
        Scene confirmationScene = new Scene(vbox);
        confirmationStage.setScene(confirmationScene);
        confirmationStage.setTitle("Confirmation de suppression");

        // Action pour le bouton "Oui"
        btnYes.setOnAction(event -> {
            commandeService.supprimer(c.getId());
            commandes.remove(c);
            afficherCommandes();
            confirmationStage.close();
            showConfirmationSuccess();
        });

        // Action pour le bouton "Non"
        btnNo.setOnAction(event -> confirmationStage.close());

        vbox.getChildren().addAll(confirmationMessage, btnYes, btnNo);
        confirmationStage.show();
    }
    private void showConfirmationSuccess() {
        // Fenêtre de succès après suppression
        VBox successBox = new VBox(15);
        successBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 15; -fx-padding: 20;");
        successBox.setAlignment(Pos.CENTER);

        Label successMessage = new Label("Le produit a été supprimé avec succès !");
        successMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");

        Button btnClose = new Button("Fermer");
        btnClose.setStyle("-fx-font-size: 14px; -fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8;");
        btnClose.setOnMouseEntered(e -> btnClose.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;"));
        btnClose.setOnMouseExited(e -> btnClose.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;"));

        btnClose.setOnAction(event -> {
            // Fermer la fenêtre de succès
            Stage stage = (Stage) btnClose.getScene().getWindow();
            stage.close();
        });

        successBox.getChildren().addAll(successMessage, btnClose);

        // Créer une nouvelle scène pour afficher le message de succès
        Stage successStage = new Stage();
        Scene successScene = new Scene(successBox);
        successStage.setScene(successScene);
        successStage.setTitle("Succès");
        successStage.show();
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCommande.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) listViewCommandes.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
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
    private void showAlerty(String title, String content) {
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
    private void afficherAlertee(String titre, String message) {
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