package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import tn.cinema.entities.Demande;

import java.sql.Date;

public class InterfaceDemandes {
    @FXML
    private TableColumn<?, ?> Actions;

    @FXML
    private TableColumn<Demande, Integer> AdminId;

    @FXML
    private TableColumn<Demande, Date> date;

    @FXML
    private TableColumn<Demande, String> description;

    @FXML
    private TableColumn<Demande, Integer> id;

    @FXML
    private TableColumn<Demande, String> liensupp;

    @FXML
    private TableColumn<Demande, Integer> nbrjours;

    @FXML
    private TableColumn<Demande, String> statut;

    @FXML
    private TableView<Demande> tableauDemande;

    @FXML
    private TableColumn<Demande, String> type;

    @FXML
    private TableColumn<Demande, Integer> userId;

}
