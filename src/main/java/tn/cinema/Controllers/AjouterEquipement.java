package tn.cinema.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;


public class AjouterEquipement {
    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfType;

    @FXML
    private TextField tfMarque;

    @FXML
    private ComboBox<String> cbEtat;


    public void setTfNom(TextField tfNom) {
        this.tfNom = tfNom;
    }

    public void setTfType(TextField tfType) {
        this.tfType = tfType;
    }

    public void setTfMarque(TextField tfMarque) {
        this.tfMarque = tfMarque;
    }

    public void setCbEtat(ComboBox<String> cbEtat) {
        this.cbEtat = cbEtat;
    }


    public void AjouteEquipement(ActionEvent actionEvent) {
    }

    public void resetForm(ActionEvent actionEvent) {
        tfNom.clear();
        tfType.clear();
        tfMarque.clear();
        cbEtat.getSelectionModel().clearSelection();
    }
    public void initialize(URL location, ResourceBundle resources) {
        cbEtat.getItems().addAll("Disponible", "En panne", "En maintenance");
    }



}