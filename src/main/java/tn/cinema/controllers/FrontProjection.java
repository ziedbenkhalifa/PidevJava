package tn.cinema.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Projection;
import tn.cinema.services.ProjectionService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FrontProjection extends FrontzController implements Initializable {

    @FXML
    private Button coursButton, demandeSubButton, filmsButton, logoutButton,
            monCompteButton, produitsButton, publiciteSubButton, publicitesButton,
            courSubButton, seanceSubButton;

    @FXML
    private ListView<Projection> listProjection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ProjectionService ps = new ProjectionService();
            List<Projection> projections = ps.recuperer();
            ObservableList<Projection> observableList = FXCollections.observableArrayList(projections);
            listProjection.setItems(observableList);

            listProjection.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Projection item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        HBox hbox = new HBox(10);
                        hbox.setStyle("-fx-padding: 10; -fx-alignment: center-left; -fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 10;");

                        VBox vbox = new VBox(5);
                        vbox.setStyle("-fx-padding: 5;");

                        Text date = new Text("Date: " + item.getDate_projection());
                        date.setStyle("-fx-font-size: 14px; -fx-fill: #000000;");

                        Text capaciter = new Text("Capacité: " + item.getCapaciter());
                        capaciter.setStyle("-fx-font-size: 14px; -fx-fill: #000000;");

                        Text prix = new Text("Prix: " + item.getPrix() + " DT");
                        prix.setStyle("-fx-font-size: 14px; -fx-fill: #000000;");

                        Text filmName = new Text(item.getFilm() != null ? "Film: " + item.getFilm().getNom_film() : "Film: Non spécifié");
                        filmName.setStyle("-fx-font-size: 14px; -fx-fill: #000000;");

                        vbox.getChildren().addAll(filmName, date, capaciter, prix);

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        Button reserveBtn = new Button("Reservation");
                        reserveBtn.setStyle("-fx-background-color: #3e2063; -fx-text-fill: white; -fx-background-radius: 10;");
                        reserveBtn.setOnAction(e -> {
                            String filmDetails = (item.getFilm() != null ? "Film: " + item.getFilm().getNom_film() : "Film: Non spécifié") +
                                    "\nDate: " + item.getDate_projection() +
                                    "\nCapacité: " + item.getCapaciter() +
                                    "\nPrix: " + item.getPrix() + " DT";

                            generateQRCode(filmDetails);
                            showAlert("Succès", "Réservation pour la projection du " + item.getDate_projection());
                        });

                        hbox.getChildren().addAll(vbox, spacer, reserveBtn);
                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            });
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des projections : " + e.getMessage());
        }
    }

    @FXML
    void filmsButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontFilm.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation vers la page Films : " + e.getMessage());
        }
    }

    @FXML
    void toggleSubButtonss(ActionEvent event) {
        boolean isVisible = demandeSubButton.isVisible();
        demandeSubButton.setVisible(!isVisible);
        publiciteSubButton.setVisible(!isVisible);
    }

    private void generateQRCode(String content) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            byte[] imageData = baos.toByteArray();

            Platform.runLater(() -> {
                Image fxImage = new Image(new ByteArrayInputStream(imageData));
                ImageView imageView = new ImageView(fxImage);
                StackPane root = new StackPane(imageView);
                Scene scene = new Scene(root, width, height);
                Stage qrStage = new Stage();
                qrStage.setTitle("QR Code - Réservation");
                qrStage.setScene(scene);
                qrStage.show();
            });
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la génération du QR code : " + e.getMessage());
        }
    }

    private void showAlertt(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}