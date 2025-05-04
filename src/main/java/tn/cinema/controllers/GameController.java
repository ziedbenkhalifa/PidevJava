package tn.cinema.controllers;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tn.cinema.entities.Produit;
import tn.cinema.services.ProduitService;
import javafx.stage.Stage;

import java.util.*;

public class GameController {

    @FXML private AnchorPane startPane;
    @FXML private AnchorPane gamePane;
    @FXML private AnchorPane endPane;

    @FXML private ImageView productImage;
    @FXML private Button option1Button;
    @FXML private Button option2Button;
    @FXML private Button option3Button;
    @FXML private Label scoreLabel;

    @FXML
    private Label finalScoreText;

    private int score = 0;
    private int round = 0;
    private final int maxRounds = 5;

    private Produit currentCorrect;
    private final ObservableList<Produit> produits = FXCollections.observableArrayList();
    private final ProduitService produitService = new ProduitService();
    private final Random rand = new Random();

    @FXML
    public void initialize() {
        produits.addAll(produitService.recuperer());

        if (produits.size() < 3) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Il faut au moins 3 produits pour lancer le jeu !");
            alert.show();
            gamePane.setVisible(false);
        } else {
            productImage.setFitWidth(300);
            productImage.setFitHeight(300);
            productImage.setPreserveRatio(true);
            showStartScreen();
        }
    }

    private void showStartScreen() {
        startPane.setVisible(true);
        gamePane.setVisible(false);
        endPane.setVisible(false);
    }

    private void showGameScreen() {
        startPane.setVisible(false);
        gamePane.setVisible(true);
        endPane.setVisible(false);
    }

    private void showEndScreen() {
        finalScoreText.setText("🔥 Resultats: " + score + " / " + maxRounds);
        endPane.setVisible(true);
        gamePane.setVisible(false);
        startPane.setVisible(false);
    }

    @FXML
    private void startGame() {
        score = 0;
        round = 0;
        scoreLabel.setText("Score: 0 | Round 0/" + maxRounds);
        showGameScreen();
        nextRound();
    }

    @FXML private void handleAnswer1() { checkAnswer(option1Button.getText(), option1Button); }
    @FXML private void handleAnswer2() { checkAnswer(option2Button.getText(), option2Button); }
    @FXML private void handleAnswer3() { checkAnswer(option3Button.getText(), option3Button); }

    private void checkAnswer(String chosen, Button clickedButton) {
        boolean correct = chosen.equals(currentCorrect.getNom());

        if (correct) {
            score++;
            clickedButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        } else {
            clickedButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        }

        scoreLabel.setText("Score: " + score + " | Round " + round + "/" + maxRounds);

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            resetButtonStyles();
            nextRound();
        });
        pause.play();
    }

    private void resetButtonStyles() {
        option1Button.setStyle("");
        option2Button.setStyle("");
        option3Button.setStyle("");
    }

    private void nextRound() {
        if (round >= maxRounds) {
            showEndScreen();
            return;
        }

        round++;
        currentCorrect = produits.get(rand.nextInt(produits.size()));
        scoreLabel.setText("Score: " + score + " | Round " + round + "/" + maxRounds);

        if (currentCorrect.getImage() != null && !currentCorrect.getImage().isEmpty()) {
            productImage.setImage(new Image("file:" + currentCorrect.getImage()));
        } else {
            productImage.setImage(null);
        }

        Set<String> options = new HashSet<>();
        options.add(currentCorrect.getNom());

        while (options.size() < 3) {
            options.add(produits.get(rand.nextInt(produits.size())).getNom());
        }

        List<String> shuffled = new ArrayList<>(options);
        Collections.shuffle(shuffled);

        option1Button.setText(shuffled.get(0));
        option2Button.setText(shuffled.get(1));
        option3Button.setText(shuffled.get(2));
    }

    @FXML
    private void playAgain() {
        startGame();
    }

    @FXML
    private void goToStartScreen() {
        showStartScreen();
    }
    @FXML
    private void handleQuit() {
        // Récupère la fenêtre (Stage) actuelle à partir d'un élément de l'interface
        Stage stage = (Stage) gamePane.getScene().getWindow();
        stage.close();
    }

}
