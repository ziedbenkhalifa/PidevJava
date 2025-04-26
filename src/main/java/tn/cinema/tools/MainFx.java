package tn.cinema.tools;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import tn.cinema.Controllers.ModifierEquipement;
import tn.cinema.Controllers.ModifierSalle;
import tn.cinema.entities.Equipement;
import tn.cinema.entities.Salle;

import java.io.IOException;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Créer un TabPane pour contenir les deux vues
        TabPane tabPane = new TabPane();

        // Charger la première vue (Équipement)
        FXMLLoader equipementLoader = new FXMLLoader(getClass().getResource("/Views/interfacePrincipale.fxml"));
        Parent equipementRoot = equipementLoader.load();
        Tab equipementTab = new Tab("interface Principale", equipementRoot);
        equipementTab.setClosable(false);

        // Charger la deuxième vue (Salle)
        FXMLLoader salleLoader = new FXMLLoader(getClass().getResource("/Views/AjouterSalle.fxml"));
        Parent salleRoot = salleLoader.load();
        Tab salleTab = new Tab("Ajouter Salle", salleRoot);
        salleTab.setClosable(false);

        // Ajouter les deux onglets au TabPane
        tabPane.getTabs().addAll(equipementTab, salleTab);

        // Ajouter la vue ListeEquipement
        FXMLLoader listeEquipementLoader = new FXMLLoader(getClass().getResource("/Views/ListeEquipement.fxml"));
        Parent listeEquipementRoot = listeEquipementLoader.load();
        Tab listeEquipementTab = new Tab("Liste Équipements", listeEquipementRoot);
        listeEquipementTab.setClosable(false);

        tabPane.getTabs().add(listeEquipementTab);

        // Configurer la scène avec le TabPane
        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Application Cinéma");
        primaryStage.show();
    }

    // Déplacer la méthode modifierEquipement à l'extérieur de la méthode start
    private void modifierEquipement(Equipement equipement) {
        System.out.println("🔧 Modifier : " + equipement.getNom());

        try {
            // Charger la fenêtre de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ModifierEquipement.fxml"));
            Parent root = loader.load();

            // Passer l'équipement sélectionné au contrôleur de la fenêtre ModifierEquipement
            ModifierEquipement modifierController = loader.getController();
            modifierController.setEquipement(equipement);  // Passer l'équipement à la fenêtre de modification

            // Ouvrir la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier un équipement");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la page ModifierEquipement");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    private void modifierSalle(Salle salle) {
        System.out.println("🔧 Modifier : " + salle.getNom_salle());  // Affiche le nom de la salle pour débogage

        try {
            // Charger la fenêtre de modification de salle (FXML)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ModifierSalle.fxml"));
            Parent root = loader.load();

            // Passer la salle sélectionnée au contrôleur de la fenêtre ModifierSalle
            ModifierSalle modifierController = loader.getController();
            modifierController.setSalle(salle);  // Passer la salle à la fenêtre de modification

            // Ouvrir la fenêtre de modification
            Stage stage = new Stage();
            stage.setTitle("Modifier une salle");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la page ModifierSalle");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


}