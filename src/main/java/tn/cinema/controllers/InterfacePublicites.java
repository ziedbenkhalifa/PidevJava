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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.cinema.entities.Publicite;
import tn.cinema.services.PubliciteService;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class InterfacePublicites extends Dashboard implements Initializable {
    @FXML private ListView<Publicite> publiciteListView;
    @FXML private TreeView<String> publiciteTreeView;
    @FXML private TabPane viewTabPane;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button resetButton;
    @FXML private ToggleButton viewToggleButton;
    @FXML private Button ajouterPubliciteButton;
    @FXML private Button backButton;
    @FXML private Button backButtonn;

    private PubliciteService publiciteService = new PubliciteService();
    private ObservableList<Publicite> publicites;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize filter ComboBox with years
        filterComboBox.getItems().add("Tous");
        filterComboBox.setValue("Tous");

        // Set up real-time search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                applyFilters();
            }
        });

        // Set up filter listener
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // Configure ListView
        publiciteListView.setCellFactory(listView -> new ListCell<Publicite>() {
            @Override
            protected void updateItem(Publicite publicite, boolean empty) {
                super.updateItem(publicite, empty);
                if (empty || publicite == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    VBox card = new VBox(15);
                    card.setStyle("-fx-background-color: #021b50;" +
                            "-fx-padding: 20;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #b0bec5;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0.5, 3, 3);");

                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(15);
                    gridPane.setVgap(10);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    String labelStyle = "-fx-font-weight: bold; -fx-font-size: 14; -fx-fill: #ffffff;";
                    String valueStyle = "-fx-font-size: 14; -fx-fill: #ffffff;";

                    Text idLabel = new Text("ID: ");
                    idLabel.setStyle(labelStyle);
                    Text idValue = new Text(String.valueOf(publicite.getId()));
                    idValue.setStyle(valueStyle);

                    Text demandeIdLabel = new Text("Demande ID: ");
                    demandeIdLabel.setStyle(labelStyle);
                    Text demandeIdValue = new Text(String.valueOf(publicite.getDemandeId()));
                    demandeIdValue.setStyle(valueStyle);

                    Text dateDebutLabel = new Text("Date Début: ");
                    dateDebutLabel.setStyle(labelStyle);
                    Text dateDebutValue = new Text(dateFormat.format(publicite.getDateDebut()));
                    dateDebutValue.setStyle(valueStyle);

                    Text dateFinLabel = new Text("Date Fin: ");
                    dateFinLabel.setStyle(labelStyle);
                    Text dateFinValue = new Text(dateFormat.format(publicite.getDateFin()));
                    dateFinValue.setStyle(valueStyle);

                    Text supportLabel = new Text("Support: ");
                    supportLabel.setStyle(labelStyle);
                    Text supportValue = new Text(publicite.getSupport());
                    supportValue.setStyle(valueStyle);

                    Text montantLabel = new Text("Montant: ");
                    montantLabel.setStyle(labelStyle);
                    Text montantValue = new Text(String.format("%.2f", publicite.getMontant()));
                    montantValue.setStyle(valueStyle);

                    gridPane.add(idLabel, 0, 0);
                    gridPane.add(idValue, 1, 0);
                    gridPane.add(demandeIdLabel, 2, 0);
                    gridPane.add(demandeIdValue, 3, 0);
                    gridPane.add(dateDebutLabel, 0, 1);
                    gridPane.add(dateDebutValue, 1, 1);
                    gridPane.add(dateFinLabel, 2, 1);
                    gridPane.add(dateFinValue, 3, 1);
                    gridPane.add(supportLabel, 0, 2);
                    gridPane.add(supportValue, 1, 2);
                    gridPane.add(montantLabel, 2, 2);
                    gridPane.add(montantValue, 3, 2);

                    HBox buttonsBox = new HBox(10);
                    buttonsBox.setStyle("-fx-padding: 15 0 0 0; -fx-alignment: center-right;");

                    Button modifierButton = new Button("Modifier");
                    modifierButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                    modifierButton.setOnMouseEntered(e -> {
                        modifierButton.setStyle("-fx-background-color: #66bb6a; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                        modifierButton.setScaleX(1.05);
                        modifierButton.setScaleY(1.05);
                    });
                    modifierButton.setOnMouseExited(e -> {
                        modifierButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                        modifierButton.setScaleX(1.0);
                        modifierButton.setScaleY(1.0);
                    });
                    modifierButton.setOnAction(event -> {
                        try {
                            openModifierPublicite(publicite);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
                            alert.showAndWait();
                        }
                    });

                    Button supprimerButton = new Button("Supprimer");
                    supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                    supprimerButton.setOnMouseEntered(e -> {
                        supprimerButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                        supprimerButton.setScaleX(1.05);
                        supprimerButton.setScaleY(1.05);
                    });
                    supprimerButton.setOnMouseExited(e -> {
                        supprimerButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-font-size: 14; -fx-padding: 5 15;");
                        supprimerButton.setScaleX(1.0);
                        supprimerButton.setScaleY(1.0);
                    });
                    supprimerButton.setOnAction(event -> {
                        try {
                            openSupprimerPublicite(publicite);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de suppression : " + e.getMessage());
                            alert.showAndWait();
                        }
                    });

                    buttonsBox.getChildren().addAll(modifierButton, supprimerButton);

                    Rectangle separator = new Rectangle();
                    separator.setHeight(1);
                    separator.setFill(Color.web("#ffffff"));
                    separator.setStyle("-fx-opacity: 0.3;");

                    card.getChildren().addAll(gridPane, buttonsBox, separator);

                    card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #031f5c;" +
                            "-fx-padding: 20;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #ffffff;" +
                            "-fx-border-width: 2;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0.7, 4, 4);"));
                    card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #021b50;" +
                            "-fx-padding: 20;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-border-color: #b0bec5;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0.5, 3, 3);"));

                    setGraphic(card);
                }
            }
        });

        // Configure TreeView
        publiciteTreeView.setCellFactory(tv -> new TreeCell<String>() {
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
                        // Year level
                        setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #ffd2ac;");
                    } else if (treeItem != null && treeItem.getParent() != null) {
                        // Publicite level
                        setStyle("-fx-font-size: 13; -fx-text-fill: white;");
                    } else {
                        // Root
                        setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #ffd2ac;");
                    }
                }
            }
        });

        loadPublicites();
    }

    private void loadPublicites() {
        try {
            publicites = FXCollections.observableArrayList(publiciteService.recupererpub());
            publiciteListView.setItems(publicites);
            updateFilterComboBox();
            updateTreeView();
            System.out.println("Nombre de publicités récupérées : " + publicites.size());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des publicités : " + e.getMessage());
        }
    }

    private void updateFilterComboBox() {
        // Get unique years from dateDebut
        Set<Integer> years = publicites.stream()
                .map(pub -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(pub.getDateDebut());
                    return cal.get(Calendar.YEAR);
                })
                .collect(Collectors.toSet());
        filterComboBox.getItems().clear();
        filterComboBox.getItems().add("Tous");
        years.stream()
                .sorted()
                .forEach(year -> filterComboBox.getItems().add(String.valueOf(year)));
        filterComboBox.setValue("Tous");
    }

    private void updateTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Toutes les Publicités");
        rootItem.setExpanded(true);

        Map<Integer, TreeItem<String>> yearItems = new HashMap<>();
        for (Publicite publicite : publicites) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(publicite.getDateDebut());
            int year = cal.get(Calendar.YEAR);
            yearItems.computeIfAbsent(year, k -> new TreeItem<>(String.valueOf(year)));

            String publiciteText = String.format("%s - %s (Début: %s)",
                    publicite.getId(), publicite.getSupport(), new SimpleDateFormat("dd/MM/yyyy").format(publicite.getDateDebut()));
            TreeItem<String> publiciteItem = new TreeItem<>(publiciteText);
            yearItems.get(year).getChildren().add(publiciteItem);
        }

        yearItems.values().stream()
                .sorted(Comparator.comparing(TreeItem::getValue))
                .forEach(item -> rootItem.getChildren().add(item));

        publiciteTreeView.setRoot(rootItem);
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String filterValue = filterComboBox.getValue();

        if (publicites == null) return;

        ObservableList<Publicite> filteredList = FXCollections.observableArrayList();

        for (Publicite publicite : publicites) {
            boolean matchesSearch = searchText.isEmpty();
            boolean matchesFilter = filterValue.equals("Tous");

            if (!searchText.isEmpty()) {
                matchesSearch = publicite.getSupport().toLowerCase().contains(searchText) ||
                        String.valueOf(publicite.getId()).contains(searchText) ||
                        String.valueOf(publicite.getMontant()).contains(searchText);
            }

            if (!filterValue.equals("Tous")) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(publicite.getDateDebut());
                int year = cal.get(Calendar.YEAR);
                matchesFilter = String.valueOf(year).equals(filterValue);
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(publicite);
            }
        }

        publiciteListView.setItems(filteredList);
        updateTreeViewWithFilteredData(filteredList);
    }

    private void updateTreeViewWithFilteredData(ObservableList<Publicite> filteredList) {
        TreeItem<String> rootItem = new TreeItem<>("Publicités filtrées");
        rootItem.setExpanded(true);

        Map<Integer, TreeItem<String>> yearItems = new HashMap<>();
        for (Publicite publicite : filteredList) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(publicite.getDateDebut());
            int year = cal.get(Calendar.YEAR);
            yearItems.computeIfAbsent(year, k -> new TreeItem<>(String.valueOf(year)));

            String publiciteText = String.format("%s - %s (Début: %s)",
                    publicite.getId(), publicite.getSupport(), new SimpleDateFormat("dd/MM/yyyy").format(publicite.getDateDebut()));
            TreeItem<String> publiciteItem = new TreeItem<>(publiciteText);
            yearItems.get(year).getChildren().add(publiciteItem);
        }

        yearItems.values().stream()
                .sorted(Comparator.comparing(TreeItem::getValue))
                .forEach(item -> rootItem.getChildren().add(item));

        publiciteTreeView.setRoot(rootItem);
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        filterComboBox.setValue("Tous");
        publiciteListView.setItems(publicites);
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
    private void onAjouterPubliciteClick() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPublicite.fxml"));
        Parent root = loader.load();
        AjouterPublicite controller = loader.getController();
        controller.setParentController(this);
        Stage stage = new Stage();
        stage.setTitle("Ajouter une Publicité");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openModifierPublicite(Publicite publicite) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierPublicite.fxml"));
        Parent root = loader.load();
        ModifierPublicite controller = loader.getController();
        controller.setPublicite(publicite);
        controller.setParentController(this);
        Stage stage = new Stage();
        stage.setTitle("Modifier la Publicité");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void openSupprimerPublicite(Publicite publicite) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SupprimerPublicite.fxml"));
        Parent root = loader.load();
        SupprimerPublicite controller = loader.getController();
        controller.setPublicite(publicite);
        controller.setParentController(this);
        Stage stage = new Stage();
        stage.setTitle("Supprimer la Publicité");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void refreshList() {
        loadPublicites();
    }

    @FXML
    private void goBackToInterfaceChoixGP() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceChoixGP.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion Publicités");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading InterfaceChoixGP.fxml: " + e.getMessage());
        }
    }

    @FXML
    public void goBackToLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButtonn.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Dashboard.fxml: " + e.getMessage());
        }
    }
}