package tn.cinema.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import tn.cinema.entities.Projection;
import tn.cinema.services.ProjectionService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FrontProjection {

    @FXML
    private Button coursButton, demandeSubButton, filmsButton, logoutButton,
            monCompteButton, produitsButton, publiciteSubButton, publicitesButton;

    @FXML
    private ListView<Projection> listProjection;

    @FXML
    void initialize() throws SQLException {
        // Example list – replace with your service call
        ProjectionService ps = new ProjectionService();
        List<Projection> projections = ps.recuperer();
        listProjection.getItems().addAll(projections);



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

                    Text prix = new Text("Prix: " + item.getPrix() + "DT");
                    prix.setStyle("-fx-font-size: 14px; -fx-fill: #040404;");

                    vbox.getChildren().addAll(date, capaciter, prix);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button reserveBtn = new Button("Reservation");
                    reserveBtn.setStyle("-fx-background-color: #3e2063; -fx-text-fill: white; -fx-background-radius: 10;");
                    reserveBtn.setOnAction(e -> {
                        // Get the film details from the Projection object
                        String filmDetails = "Film: " + item.getFilm().getNom_film() + // Assuming Projection has Film object
                                "\nDate: " + item.getDate_projection() +
                                "\nCapacité: " + item.getCapaciter() +
                                "\nPrix: " + item.getPrix() + " DT";

                        // Call the method to generate the QR code
                        generateQRCode(filmDetails);

                        // Optionally, show an alert
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Réservation pour la projection du " + item.getDate_projection());
                        alert.showAndWait();
                    });


                    hbox.getChildren().addAll(vbox, spacer, reserveBtn);
                    setGraphic(hbox);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    @FXML
    void filmsButtonClicked(javafx.event.ActionEvent event) {}

    @FXML
    void goToDemandeClient(javafx.event.ActionEvent event) {}

    @FXML
    void goToPubliciteClient(javafx.event.ActionEvent event) {}

    @FXML
    void logout(javafx.event.ActionEvent event) {}

    @FXML
    void toggleSubButtons(javafx.event.ActionEvent event) {
        demandeSubButton.setVisible(!demandeSubButton.isVisible());
        publiciteSubButton.setVisible(!publiciteSubButton.isVisible());
    }



    private void generateQRCode(String content) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;

        try {
            // Generate QR code as a bit matrix
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convert BufferedImage to ByteArrayInputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byte[] imageData = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
            byte[] finalImageData = imageData; // make it effectively final for lambda

            Platform.runLater(() -> {
                ByteArrayInputStream inputForFX = new ByteArrayInputStream(finalImageData);
                Image fxImage = new Image(inputForFX);
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
        }
    }


}
