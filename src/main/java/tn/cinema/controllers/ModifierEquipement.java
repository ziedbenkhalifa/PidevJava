package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.cinema.entities.Equipement;
import tn.cinema.services.EquipementService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class ModifierEquipement {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfType;

    @FXML
    private TextField tfSalle;

    @FXML
    private ComboBox<String> cbEtat;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnReset;

    private Equipement equipementActuel;

    public void initialize() {
        cbEtat.getItems().addAll("Neuf", "Fonctionnel", "Défectueux");
        btnModifier.setOnAction(this::ModifierEquipement);
        btnReset.setOnAction(event -> resetForm());
    }

    public void setEquipement(Equipement equipement) {
        equipementActuel = equipement;
        tfNom.setText(equipement.getNom());
        tfType.setText(equipement.getType());
        tfSalle.setText(String.valueOf(equipement.getSalle_id()));
        cbEtat.setValue(equipement.getEtat());
    }

    @FXML
    private void ModifierEquipement(ActionEvent event) {
        String nom = tfNom.getText();
        String type = tfType.getText();
        String salle = tfSalle.getText();
        String etat = cbEtat.getValue();

        try {
            int salleId = Integer.parseInt(salle);

            // Créer l'objet avec l'ID original
            Equipement equipementModifie = new Equipement(nom, type, salleId, etat);
            equipementModifie.setId(equipementActuel.getId()); // garder l'ID de l'équipement actuel

            EquipementService service = new EquipementService();
            service.modifier(equipementModifie); // appel sans retour car void

            // ✅ Message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("✔ Équipement modifié avec succès !");
            successAlert.showAndWait(); // Attendre que l'utilisateur clique sur OK

            // ✅ Redirection vers la liste après confirmation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ListeEquipement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnModifier.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (NumberFormatException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("🚫 Le champ 'Salle' doit contenir un nombre entier !");
            errorAlert.showAndWait();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Une erreur s'est produite");
            errorAlert.setContentText("Impossible de modifier l'équipement. Veuillez réessayer.");
            errorAlert.showAndWait();
        }
    }


    @FXML
    public void resetForm() {
        tfNom.clear();
        tfType.clear();
        tfSalle.clear();
        cbEtat.setValue(null);
    }
}