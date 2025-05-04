package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import tn.cinema.entities.Produit;


public class FavorisController {

    @FXML
    private ListView<HBox> listViewFavoris;


    // Liste passée depuis l'autre controller
    private ObservableList<Produit> favoris;

    /**
     * Ce setter sera appelé depuis ListProduits juste après le FXMLLoader.load()
     */
    public void setFavorisList(ObservableList<Produit> favoris) {
        this.favoris = favoris;
        afficherFavoris();
    }

    private void afficherFavoris() {
        listViewFavoris.getItems().clear();

        if (favoris == null) return;  // sécurité

        for (Produit p : favoris) {
            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setPadding(new Insets(10));

            // Image
            ImageView iv = new ImageView(new Image("file:" + p.getImage(), 50, 50, true, true));

            // Nom et prix
            Label lblNom = new Label(p.getNom());
            lblNom.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");
            Label lblPrix = new Label(p.getPrix() + " DT");
            lblPrix.setStyle("-fx-text-fill:#27ae60;");

            // Bouton supprimer
            Button btnRemove = new Button("Supprimer");
            btnRemove.setStyle("-fx-background-color:#e74c3c; -fx-text-fill:white;");
            btnRemove.setOnAction(e -> {
                favoris.remove(p);
                afficherFavoris();
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            hbox.getChildren().addAll(iv, lblNom, lblPrix, spacer, btnRemove);
            listViewFavoris.getItems().add(hbox);
        }
    }

    // Méthode pour supprimer un produit des favoris
    private void supprimerDuFavoris(Produit produit) {
        favoris.remove(produit); // Supprimer le produit de la liste des favoris
        afficherFavoris(); // Mettre à jour la ListView après la suppression
    }
}

