package tn.cinema.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.cinema.entities.Cour;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AfficherDetailsCour {

    private static final Logger LOGGER = Logger.getLogger(AfficherDetailsCour.class.getName());

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private Button courSubButton;

    private Cour currentCour;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) qrCodeImageView.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                LOGGER.info("Fermeture de l'application détectée.");
            });
        });
    }

    public void loadDetails(Cour cour) {
        this.currentCour = cour;

        try {
            // Générer le contenu du QR code avec les détails du cours
            String qrContent = String.format(
                    "Cours ID: %d\nType: %s\nCout: %s DT\nDebut: %s\nFin: %s",
                    cour.getId(),
                    cour.getTypeCour() != null ? cour.getTypeCour() : "Non défini",
                    cour.getCout(),
                    cour.getDateDebut() != null ? cour.getDateDebut().format(DATE_FORMATTER) : "Non défini",
                    cour.getDateFin() != null ? cour.getDateFin().format(DATE_FORMATTER) : "Non défini"
            );
            LOGGER.info("Génération du QR code avec contenu : " + qrContent);
            Image qrCodeImage = generateQRCode(qrContent, 200, 200);
            qrCodeImageView.setImage(qrCodeImage);
        } catch (WriterException | IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la génération du QR code", e);
            showAlert("Erreur", "Erreur lors de la génération du QR code : " + e.getMessage());
        }
    }

    private Image generateQRCode(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pngOutputStream.toByteArray());
        return new Image(inputStream);
    }

    @FXML
    private void retourAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            Parent root = loader.load();
            AffichageListCoursFront controller = loader.getController();
            controller.refreshParticipations();
            controller.displayCours(controller.allCours);

            Stage stage = (Stage) qrCodeImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la navigation retour", e);
            showAlert("Erreur", "Erreur lors de la navigation : " + e.getMessage());
        }
    }

    @FXML
    private void goCourAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListCoursFront.fxml"));
            Parent root = loader.load();
            AffichageListCoursFront controller = loader.getController();
            controller.refreshParticipations();
            controller.displayCours(controller.allCours);

            Stage stage = (Stage) qrCodeImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la navigation vers la page Cours", e);
            showAlert("Erreur", "Erreur lors de la navigation vers la page Cours : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goSeanceAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageListSeancesFront.fxml"));
            Parent root = loader.load();
            AffichageListSeancesFront controller = loader.getController();
            controller.initializeWithCour(currentCour); // Passer le cours actuel
            Stage stage = (Stage) qrCodeImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture du calendrier des séances", e);
            showAlert("Erreur", "Erreur lors de l'ouverture du calendrier : " + e.getMessage());
        }
    }
}