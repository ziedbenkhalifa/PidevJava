package tn.cinema.test;

import tn.cinema.entities.Demande;
import tn.cinema.entities.Publicite;
import tn.cinema.services.DemandeService;
import tn.cinema.services.PubliciteService;
import java.sql.Date;
import tn.cinema.tools.Mydatabase;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args){
     //   Mydatabase connexion = Mydatabase.getInstance();
        DemandeService ps = new DemandeService();
        PubliciteService publiciteService = new PubliciteService();
        /*Demande p = new Demande(27,"romdhane","romdhane","romdhane");
        try {
            ps.ajouter(p);
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        }*/

     /*   String debutt="2025-03-12";
        String finn="2025-03-13";
        Date datedebutt=Date.valueOf(debutt);
        Date datefinn=Date.valueOf(finn);
        Publicite pub = new Publicite(50,datedebutt,datefinn,"300",800);
        try {
            publiciteService.ajouterpub(pub);
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        }*/

        int idClient = 3;


        try {
            List<Demande> demandesClient = ps.recupererDemandesParClient(idClient);
            System.out.println("Demandes du client " + idClient + ":");
            if (demandesClient.isEmpty()) {
                System.out.println("Aucune demande trouvée pour ce client.");
            } else {
                for (Demande demande : demandesClient) {
                    System.out.println("ID: " + demande.getId() +
                            ", Description: " + demande.getDescription() +
                            ", Type: " + demande.getType() +
                            ", Statut: " + demande.getStatut());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des demandes du client: " + e.getMessage());
        }

        try {
            List<Publicite> publicitesClient = publiciteService.recupererPublicitesParClient(idClient);
            System.out.println("Publicités du client " + idClient + ":");
            if (publicitesClient.isEmpty()) {
                System.out.println("Aucune publicité trouvée pour ce client.");
            } else {
                for (Publicite pub : publicitesClient) {
                    System.out.println("ID: " + pub.getId() +
                            ", Demande ID: " + pub.getDemandeId() +
                            ", Date Début: " + pub.getDateDebut() +
                            ", Date Fin: " + pub.getDateFin() +
                            ", Support: " + pub.getSupport() +
                            ", Montant: " + pub.getMontant());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des publicités du client: " + e.getMessage());
        }

        int demandeIdToModify = 45;
        Demande updatedDemande = new Demande(3, 30, "Updated description", "Updated type", "http://updatedlink.com");
        try {
            ps.modifier(demandeIdToModify, updatedDemande);
        } catch (SQLException e) {
            System.out.println("Error modifying Demande: " + e.getMessage());
        }
        int publiciteIdToModify = 13;
        String debut="2025-03-12";
        String fin="2025-03-13";
        Date datedebut=Date.valueOf(debut);
        Date datefin=Date.valueOf(fin);

        Publicite updatedPublicite = new Publicite(datedebut,datefin,"https://bnaimage",800);
        try {
            publiciteService.modifierpub(publiciteIdToModify, updatedPublicite);
        } catch (SQLException e) {
            System.out.println("Error modifying Demande: " + e.getMessage());
        }
        try {
            System.out.println(ps.recuperer());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            System.out.println(publiciteService.recupererpub());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int idASupprimer = 46;
        try {
            ps.supprimer(idASupprimer);
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }

        int idASupprimerpub = 12;
        try {
            publiciteService.supprimerpub(idASupprimerpub);
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }


    }





