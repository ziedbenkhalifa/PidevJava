package tn.cinema.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import tn.cinema.entities.Salle;
import tn.cinema.services.SalleService;
import tn.cinema.services.NotificationService; // 🛠️ Ajouté
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection; // 🛠️ Ajouté
import tn.cinema.tools.Mydatabase; // 🛠️ Changé
import java.sql.PreparedStatement; // 🛠️ Ajouté
import java.sql.Timestamp; // 🛠️ Ajouté
import java.time.LocalDateTime; // 🛠️ Ajouté

public class ModifierSalle {

    @FXML
    private TextField tfNomSalle;

    @FXML
    private ComboBox<String> cbDisponibilite;

    @FXML
    private ComboBox<String> cbEmplacement;

    @FXML
    private ComboBox<String> cbStatut;

    @FXML
    private ComboBox<String> tfTypeSalle; // Correction du nom du ComboBox

    @FXML
    private TextField tfPlaces;

    private Salle salleActuelle;

    public void setSalle(Salle salle) {
        this.salleActuelle = salle;

        tfNomSalle.setText(salle.getNom_salle());
        cbDisponibilite.setValue(salle.getDisponibilite());
        cbEmplacement.setValue(salle.getEmplacement());
        cbStatut.setValue(salle.getStatut());
        tfTypeSalle.setValue(salle.getType_salle());
        tfPlaces.setText(String.valueOf(salle.getNombre_de_place()));
    }

    @FXML
    public void ModifierSalle() {
        if (salleActuelle == null) return;

        if (tfNomSalle.getText().isEmpty() || cbDisponibilite.getValue() == null || cbEmplacement.getValue() == null
                || cbStatut.getValue() == null || tfTypeSalle.getValue() == null || tfPlaces.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        int nombreDePlaces;
        try {
            nombreDePlaces = Integer.parseInt(tfPlaces.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Valeur incorrecte");
            alert.setHeaderText("Le nombre de places doit être un entier.");
            alert.showAndWait();
            return;
        }

        salleActuelle.setNom_salle(tfNomSalle.getText());
        salleActuelle.setDisponibilite(cbDisponibilite.getValue());
        salleActuelle.setEmplacement(cbEmplacement.getValue());
        salleActuelle.setStatut(cbStatut.getValue());
        salleActuelle.setType_salle(tfTypeSalle.getValue());
        salleActuelle.setNombre_de_place(nombreDePlaces);

        String statut = salleActuelle.getStatut();
        if ("Fermée".equalsIgnoreCase(statut) || "En Maintenance".equalsIgnoreCase(statut)) {
            String message = "La salle \"" + salleActuelle.getNom_salle() + "\" est actuellement en " + statut.toLowerCase() + ".";

            // 🔵 Envoyer vers la liste
            NotificationService.getInstance().ajouterNotification(message);

            // 🔵 Enregistrer aussi dans la base de données
            enregistrerNotificationDansBase(message);
        }

        try {
            SalleService service = new SalleService();
            service.modifier(salleActuelle);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Salle modifiée");
            alert.setContentText("Les informations de la salle ont été mises à jour.");
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ListeSalle.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tfNomSalle.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Modification échouée");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Impossible de charger la page ListeSalle");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void enregistrerNotificationDansBase(String message) {
        Connection connexion = null;
        PreparedStatement ps = null;
        try {
            connexion = Mydatabase.getInstance().getCnx();

            if (connexion == null || connexion.isClosed()) {
                System.out.println("La connexion est fermée !");
                return;
            }

            // Créer les timestamps proprement
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expires = now.plusDays(1);

            Timestamp createdAt = Timestamp.valueOf(now);
            Timestamp expiresAt = Timestamp.valueOf(expires);

            String sql = "INSERT INTO notification (message, created_at, expires_at) VALUES (?, ?, ?)";
            ps = connexion.prepareStatement(sql);
            ps.setString(1, message);
            ps.setTimestamp(2, createdAt);
            ps.setTimestamp(3, expiresAt);

            ps.executeUpdate();
            System.out.println("✅ Notification enregistrée avec expiration après un jour !");

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'enregistrement : " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("❌ Erreur lors de la fermeture de la requête : " + e.getMessage());
            }
        }}

    @FXML
    public void resetForm(ActionEvent actionEvent) {
        tfNomSalle.clear();
        tfPlaces.clear();
        cbDisponibilite.getSelectionModel().clearSelection();
        cbEmplacement.getSelectionModel().clearSelection();
        cbStatut.getSelectionModel().clearSelection();
        tfTypeSalle.getSelectionModel().clearSelection();
    }
}