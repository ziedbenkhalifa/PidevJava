package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Demande;
import tn.cinema.services.DemandeService;
import tn.cinema.services.PubliciteService;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class InterfaceDemandes extends Dashboard implements Initializable {
    @FXML private ListView<Demande> demandeListView;
    @FXML private TreeView<String> demandeTreeView;
    @FXML private TabPane viewTabPane;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button resetButton;
    @FXML private ToggleButton viewToggleButton;
    @FXML private Button ajouterDemandeButton;
    @FXML private Button backButton;

    private DemandeService demandeService = new DemandeService();
    private PubliciteService publiciteService;
    private ObservableList<Demande> demandes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            publiciteService = new PubliciteService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialiser le ComboBox de filtrage
        filterComboBox.getItems().addAll("Tous", "Approuvée", "En attente", "Rejetée");
        filterComboBox.setValue("Tous");

        // Configurer la recherche en temps réel
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                applyFilters();
            }
        });

        // Configurer le filtre
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // Configurer le ListView
        demandeListView.setCellFactory(listView -> new ListCell<Demande>() {
            @Override
            protected void updateItem(Demande demande, boolean empty) {
                super.updateItem(demande, empty);
                if (empty || demande == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    VBox vbox = new VBox(10);
                    vbox.setStyle("-fx-background-color: #021b50;" +
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #064625;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");

                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(10);
                    gridPane.setVgap(5);

                    Text idText = new Text("ID: " + demande.getId());
                    Text emailText = new Text("Email: " + demande.getEmail());
                    Text nbJoursText = new Text("Nb Jours: " + demande.getNombreJours());
                    Text descriptionText = new Text("Description: " + demande.getDescription());
                    descriptionText.setWrappingWidth(300);
                    Text typeText = new Text("Type: " + demande.getType());
                    Text lienSuppText = new Text("Lien Supp: " + (demande.getLienSupplementaire() != null ? demande.getLienSupplementaire() : "N/A"));
                    lienSuppText.setWrappingWidth(300);
                    Text statutText = new Text("Statut: " + transformStatutForUI(demande.getStatut()));
                    Text dateText = new Text("Date: " + demande.getDateSoumission());

                    for (Text text : new Text[]{idText, emailText, nbJoursText, descriptionText, typeText, lienSuppText, statutText, dateText}) {
                        text.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
                    }

                    gridPane.add(idText, 0, 0);
                    gridPane.add(emailText, 1, 0);
                    gridPane.add(nbJoursText, 0, 1);
                    gridPane.add(descriptionText, 1, 1, 2, 1);
                    gridPane.add(typeText, 0, 2);
                    gridPane.add(lienSuppText, 1, 2, 2, 1);
                    gridPane.add(statutText, 0, 3);
                    gridPane.add(dateText, 1, 3);

                    HBox buttonsBox = new HBox(10);
                    buttonsBox.setStyle("-fx-padding: 10 0 0 0; -fx-alignment: center-right;");

                    Button modifierButton = new Button("Modifier");
                    modifierButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;");
                    modifierButton.setOnMouseEntered(e -> modifierButton.setStyle("-fx-background-color: #34c759; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;"));
                    modifierButton.setOnMouseExited(e -> modifierButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;"));
                    modifierButton.setOnAction(event -> {
                        try {
                            openModifierDemande(demande);
                        } catch (Exception e) {
                            e.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage()).showAndWait();
                        }
                    });

                    Button supprimerButton = new Button("Supprimer");
                    supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;");
                    supprimerButton.setOnMouseEntered(e -> supprimerButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;"));
                    supprimerButton.setOnMouseExited(e -> supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14;"));
                    supprimerButton.setOnAction(event -> {
                        try {
                            openSupprimerDemande(demande);
                        } catch (Exception e) {
                            e.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de suppression : " + e.getMessage()).showAndWait();
                        }
                    });

                    boolean isApprouvee = "approuvee".equalsIgnoreCase(demande.getStatut().trim());
                    boolean hasPublicite = false;
                    try {
                        hasPublicite = publiciteService != null && publiciteService.existsByDemandeId(demande.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isApprouvee && !hasPublicite) {
                        Button ajouterPubliciteButton = new Button("Ajouter Publicité");
                        ajouterPubliciteButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14; -fx-padding: 5 15;");
                        ajouterPubliciteButton.setOnMouseEntered(e -> ajouterPubliciteButton.setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14; -fx-padding: 5 15;"));
                        ajouterPubliciteButton.setOnMouseExited(e -> ajouterPubliciteButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14; -fx-padding: 5 15;"));
                        ajouterPubliciteButton.setOnAction(event -> {
                            try {
                                openAjouterPubliciteFromDemande(demande);
                            } catch (Exception e) {
                                e.printStackTrace();
                                new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre Ajouter Publicité : " + e.getMessage()).showAndWait();
                            }
                        });
                        buttonsBox.getChildren().addAll(modifierButton, supprimerButton, ajouterPubliciteButton);
                    } else {
                        buttonsBox.getChildren().addAll(modifierButton, supprimerButton);
                    }

                    vbox.getChildren().addAll(gridPane, buttonsBox);

                    vbox.setOnMouseEntered(e -> vbox.setStyle("-fx-background-color: #032264;" +
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #064625;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0.7, 3, 3);"));
                    vbox.setOnMouseExited(e -> vbox.setStyle("-fx-background-color: #021b50;" +
                            "-fx-padding: 15;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #064625;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);"));

                    setGraphic(vbox);
                }
            }
        });

        // Configurer le TreeView
        demandeTreeView.setCellFactory(tv -> new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    TreeItem<String> treeItem = getTreeItem();
                    if (treeItem != null && treeItem.getParent() != null && treeItem.getParent().getParent() == null) {
                        // Niveau statut
                        setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #ffd2ac;");
                    } else if (treeItem != null && treeItem.getParent() != null) {
                        // Niveau demande
                        setStyle("-fx-font-size: 13; -fx-text-fill: white;");
                    } else {
                        // Racine
                        setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #ffd2ac;");
                    }
                }
            }
        });

        loadDemandes();
    }

    private void loadDemandes() {
        try {
            demandes = FXCollections.observableArrayList(demandeService.recuperer());
            demandeListView.setItems(demandes);
            updateTreeView();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des demandes: " + e.getMessage()).showAndWait();
        }
    }

    private void updateTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Toutes les Demandes");
        rootItem.setExpanded(true);

        // Grouper par statut
        Map<String, TreeItem<String>> statutItems = new HashMap<>();
        statutItems.put("Approuvée", new TreeItem<>("Approuvée"));
        statutItems.put("En attente", new TreeItem<>("En attente"));
        statutItems.put("Rejetée", new TreeItem<>("Rejetée"));

        for (Demande demande : demandes) {
            String statut = transformStatutForUI(demande.getStatut());
            String demandeText = String.format("%s - %s (Jours: %d)", demande.getEmail(),
                    demande.getDescription(), demande.getNombreJours());
            TreeItem<String> demandeItem = new TreeItem<>(demandeText);
            statutItems.get(statut).getChildren().add(demandeItem);
        }

        for (TreeItem<String> item : statutItems.values()) {
            rootItem.getChildren().add(item);
        }

        demandeTreeView.setRoot(rootItem);
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String filterValue = filterComboBox.getValue();

        if (demandes == null) return;

        ObservableList<Demande> filteredList = FXCollections.observableArrayList();

        for (Demande demande : demandes) {
            boolean matchesSearch = searchText.isEmpty();
            boolean matchesFilter = filterValue.equals("Tous");

            // Filtre de recherche
            if (!searchText.isEmpty()) {
                matchesSearch = demande.getEmail().toLowerCase().contains(searchText) ||
                        demande.getDescription().toLowerCase().contains(searchText) ||
                        demande.getType().toLowerCase().contains(searchText) ||
                        (demande.getLienSupplementaire() != null &&
                                demande.getLienSupplementaire().toLowerCase().contains(searchText));
            }

            // Filtre par statut
            if (!filterValue.equals("Tous")) {
                String statutUI = transformStatutForUI(demande.getStatut());
                matchesFilter = statutUI.equals(filterValue);
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(demande);
            }
        }

        demandeListView.setItems(filteredList);
        updateTreeViewWithFilteredData(filteredList);
    }

    private void updateTreeViewWithFilteredData(ObservableList<Demande> filteredList) {
        TreeItem<String> rootItem = new TreeItem<>("Demandes filtrées");
        rootItem.setExpanded(true);

        Map<String, TreeItem<String>> statutItems = new HashMap<>();
        statutItems.put("Approuvée", new TreeItem<>("Approuvée"));
        statutItems.put("En attente", new TreeItem<>("En attente"));
        statutItems.put("Rejetée", new TreeItem<>("Rejetée"));

        for (Demande demande : filteredList) {
            String statut = transformStatutForUI(demande.getStatut());
            String demandeText = String.format("%s - %s (Jours: %d)", demande.getEmail(),
                    demande.getDescription(), demande.getNombreJours());
            TreeItem<String> demandeItem = new TreeItem<>(demandeText);
            statutItems.get(statut).getChildren().add(demandeItem);
        }

        for (TreeItem<String> item : statutItems.values()) {
            if (!item.getChildren().isEmpty()) {
                rootItem.getChildren().add(item);
            }
        }

        demandeTreeView.setRoot(rootItem);
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        filterComboBox.setValue("Tous");
        demandeListView.setItems(demandes);
        updateTreeView();
    }

    @FXML
    private void toggleView() {
        if (viewTabPane.getSelectionModel().getSelectedIndex() == 0) {
            viewTabPane.getSelectionModel().select(1);
            viewToggleButton.setText("Vue Liste");
        } else {
            viewTabPane.getSelectionModel().select(0);
            viewToggleButton.setText("Vue Arborescence");
        }
    }

    @FXML
    private void onAjouterDemandeClick() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterDemande.fxml"));
        Parent root = loader.load();
        AjouterDemande controller = loader.getController();
        controller.setParentController(this);
        Stage stage = new Stage();
        stage.setTitle("Ajouter une Demande");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openModifierDemande(Demande demande) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDemande.fxml"));
        Parent root = loader.load();
        ModifierDemande controller = loader.getController();
        controller.setDemande(demande);
        controller.setParentController(this);
        Stage stage = new Stage();
        stage.setTitle("Modifier la Demande");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openSupprimerDemande(Demande demande) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SupprimerDemande.fxml"));
        Parent root = loader.load();
        SupprimerDemande controller = loader.getController();
        controller.setDemande(demande);
        controller.setParentController(this);
        Stage stage = new Stage();
        stage.setTitle("Supprimer la Demande");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openAjouterPubliciteFromDemande(Demande demande) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPublicite.fxml"));
        Parent root = loader.load();
        AjouterPublicite controller = loader.getController();
        controller.setParentController(this);
        controller.setDemandeId(demande.getId());
        Stage stage = new Stage();
        stage.setTitle("Ajouter une Publicité");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void refreshList() {
        loadDemandes();
    }

    private String transformStatutForUI(String statut) {
        if (statut == null) return "Inconnu";
        switch (statut.toLowerCase()) {
            case "approuvee": return "Approuvée";
            case "en_attente": return "En attente";
            case "rejete": return "Rejetée";
            default: return statut;
        }
    }

    @FXML
    private void goBackToInterfaceChoixGP() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceChoixGP.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion Publicités");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}