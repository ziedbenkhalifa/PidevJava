package tn.cinema.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tn.cinema.entities.Cour;
import tn.cinema.entities.User;
import tn.cinema.services.CourService;
import tn.cinema.utils.SessionManager;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PopupParticipationController {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox cardsContainer;
    @FXML
    private Button closeButton;

    private Stage stage;
    private List<Cour> participations;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void setParticipations(List<Cour> participations) {
        this.participations = participations;
        populateCards();
    }

    private void populateCards() {

        cardsContainer.getChildren().clear();

        if (participations == null || participations.isEmpty()) {
            Label emptyLabel = new Label("Aucune participation trouvée.");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
            cardsContainer.getChildren().add(emptyLabel);
            return;
        }


        for (Cour cour : participations) {

            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: #34495e; -fx-background-radius: 8; -fx-padding: 15;");
            card.setEffect(new DropShadow(5, javafx.scene.paint.Color.color(0, 0, 0, 0.33)));
            card.setPrefWidth(280);


            Label titleLabel = new Label(cour.getTypeCour());
            titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            titleLabel.setFont(new Font("System Bold", 16));
            titleLabel.setStyle("-fx-font-size: 16px;");


            Label detailsLabel = new Label(
                    "Coût: " + cour.getCout() + " DT\n" +
                            "Date Début: " + cour.getDateDebut().format(FORMATTER) + "\n" +
                            "Date Fin: " + cour.getDateFin().format(FORMATTER)
            );
            detailsLabel.setStyle("-fx-text-fill: white;");
            detailsLabel.setFont(new Font(14));
            detailsLabel.setWrapText(true);

            card.getChildren().addAll(titleLabel, detailsLabel);
            cardsContainer.getChildren().add(card);
        }
    }

    @FXML
    private void handleClose() {

        if (stage != null) {
            stage.close();
        }
    }
}
