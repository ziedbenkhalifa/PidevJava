package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;

import java.sql.SQLException;


public class AjouterDemandes {

    @FXML
    private TextArea description;

    @FXML
    private TextField liensupp;

    @FXML
    private TextField nbrjours;

    @FXML
    private TextField type;
    private DemandeService ps = new DemandeService();
    @FXML
    void save(ActionEvent event)  {
    try{
    ps.ajouter(new Demande(Integer.parseInt(nbrjours.getText()),description.getText(),type.getText(),liensupp.getText()));
    }catch(SQLException e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("erreur");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        }
    }
}
