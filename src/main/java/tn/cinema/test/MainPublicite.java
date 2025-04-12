package tn.cinema.test;

import tn.cinema.entities.Demande;
import tn.cinema.entities.Publicite;
import tn.cinema.services.DemandeService;
import tn.cinema.services.PubliciteService;
import java.sql.Date;

import java.sql.SQLException;
import java.util.List;

public class MainPublicite {
    public static void main(String[] args){
        //   Mydatabase connexion = Mydatabase.getInstance();
        DemandeService ps = new DemandeService();
        PubliciteService publiciteService = new PubliciteService();
        try {
            List<Demande> toutesLesDemandes = ps.recuperer();  // Appel de la méthode recuperer()
            System.out.println("Toutes les demandes de tous les clients :");
            if (toutesLesDemandes.isEmpty()) {
                System.out.println("Aucune demande trouvée.");
            } else {
                for (Demande demande : toutesLesDemandes) {
                    System.out.println("ID: " + demande.getId() +
                            ", ID client: " + demande.getUserId() +
                            ", ID admin: " + demande.getAdminId() +
                            ", Nombre jours: " + demande.getNombreJours() +
                            ", Description: " + demande.getDescription() +
                            ", Type: " + demande.getType() +
                            ", lien du support: " + demande.getLienSupplementaire() +
                            ", Statut: " + demande.getStatut() +
                            ", Date Soumission: " + demande.getDateSoumission());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des demandes : " + e.getMessage());
        }
        try {
            System.out.println(publiciteService.recupererpub());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
                            ", nombre jours: " + demande.getNombreJours() +
                            ", Description: " + demande.getDescription() +
                            ", Type: " + demande.getType() +
                            ", Statut: " + demande.getStatut() +
                            ", date soumission: " + demande.getDateSoumission());

                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des demandes du client: " + e.getMessage());
        }
        int idClientdem = 3;
        try {
            List<Publicite> publicitesClient = publiciteService.recupererPublicitesParClient(idClientdem);
            System.out.println("Publicités du client " + idClientdem + ":");
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
        Demande updatedDemande = new Demande( 60, "Updated deskkkkk", "Updated deskkkkkk", "http://updatedlink.tn");
        try {
            ps.modifier(demandeIdToModify, updatedDemande);
        } catch (SQLException e) {
            System.out.println("Error modifying Demande: " + e.getMessage());
        }
      /* int demandeIdToModifyadmin = 45;
        Demande updatedDemandes = new Demande(20, "hello", "word", "http://updatedlink.com","en_attente");
        try {
            ps.modifierAdmin(demandeIdToModifyadmin, updatedDemandes);
        } catch (SQLException e) {
            System.out.println("Error modifying Demande: " + e.getMessage());
        }*/

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
       /* try {
            System.out.println(ps.recuperer());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/


        int idASupprimerdem = 46;
        try {
            ps.supprimer(idASupprimerdem);
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





