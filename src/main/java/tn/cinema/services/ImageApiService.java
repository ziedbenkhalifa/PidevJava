package tn.cinema.services;



import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ImageApiService {

    /**
     * Construit l'URL de Placeholder.com pour générer une vignette 50×50 px
     * contenant le texte spécifié.
     */
    public static String generateEquipementImageUrl(String nomEquipement) {
        try {
            String texte = URLEncoder.encode(nomEquipement, StandardCharsets.UTF_8.toString());
            return String.format("https://via.placeholder.com/50x50.png?text=%s", texte);
        } catch (UnsupportedEncodingException ex) {
            // En cas d’échec d’encodage, on remplace juste les espaces
            return "https://via.placeholder.com/50x50.png?text="
                    + nomEquipement.replace(" ", "+");
        }
    }
}

