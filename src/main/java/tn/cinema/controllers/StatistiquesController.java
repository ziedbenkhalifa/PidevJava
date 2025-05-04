package tn.cinema.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import tn.cinema.entities.User;
import tn.cinema.services.UserService;
import tn.cinema.utils.SessionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StatistiquesController {

    @FXML
    private PieChart rolePieChart;

    @FXML
    private Button monCompteButton;

    @FXML
    private Button backToUsersButton;

    private UserService userService = new UserService();

    // Définir une map pour associer chaque rôle à une couleur spécifique
    private final Map<String, String> roleColors = new HashMap<>();

    // Définir les rôles valides
    private final Set<String> validRoles = Set.of("Admin", "Client", "Coach", "Sponsor");

    @FXML
    public void initialize() {
        // Initialiser les couleurs pour les 4 rôles : Admin, Client, Coach, Sponsor
        roleColors.put("Admin", "#294478");   // Bleu
        roleColors.put("Client", "#1e7e34");  // Vert
        roleColors.put("Coach", "#791d1d");   // Rouge
        roleColors.put("Sponsor", "#3e2063"); // Violet

        loadStatistics();
    }

    private void loadStatistics() {
        try {
            List<User> users = userService.recuperer();
            if (users.isEmpty()) {
                showAlert("Information", "Aucun utilisateur trouvé dans la base de données.");
                return;
            }

            // Débogage : Afficher tous les rôles trouvés
            System.out.println("Rôles trouvés dans la base de données :");
            users.forEach(user -> System.out.println("Rôle : " + user.getRole()));

            // Grouper les utilisateurs par rôle et filtrer uniquement les rôles valides
            Map<String, Long> roleCounts = users.stream()
                    .filter(user -> user.getRole() != null)
                    .filter(user -> validRoles.contains(user.getRole())) // Filtrer uniquement Admin, Client, Coach, Sponsor
                    .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));

            // Créer les données pour le PieChart
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            roleCounts.forEach((role, count) -> {
                pieChartData.add(new PieChart.Data(role + " (" + count + ")", count));
            });

            // Si aucune donnée valide, afficher un message
            if (pieChartData.isEmpty()) {
                showAlert("Information", "Aucun rôle valide (Admin, Client, Coach, Sponsor) trouvé.");
                return;
            }

            // Appliquer les données au PieChart
            rolePieChart.setData(pieChartData);

            // Appliquer les couleurs spécifiques à chaque rôle
            for (PieChart.Data data : rolePieChart.getData()) {
                String role = data.getName().split(" \\(")[0]; // Extraire le nom du rôle (ex. "Admin (5)" -> "Admin")
                String color = roleColors.getOrDefault(role, "#d7d7d9"); // Couleur par défaut si rôle non trouvé
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
                // Ajouter une classe CSS pour la légende
                data.getNode().getStyleClass().add("pie-" + role.toLowerCase());
            }

            // Personnaliser le style des étiquettes (la légende est désactivée dans le FXML)
            rolePieChart.setStyle("-fx-pie-label-fill: white; -fx-font-size: 14;");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec du chargement des statistiques : " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backToUsersButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger AfficherUser.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleMonCompteAction() {
        if (SessionManager.getInstance().getLoggedInUser() == null) {
            showAlert("Erreur", "Aucun utilisateur connecté.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MonCompte.fxml"));
            Parent root = loader.load();
            MonCompte controller = loader.getController();
            controller.setLoggedInUser(SessionManager.getInstance().getLoggedInUser());
            Stage stage = (Stage) monCompteButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Compte");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page Mon Compte: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}