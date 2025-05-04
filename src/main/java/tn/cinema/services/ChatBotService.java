package tn.cinema.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ChatBotService {

    private final String apiKey = "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"; // <<=== حط API KEY هنا
    private final String apiUrl = "https://api.groq.com/openai/v1/chat/completions";

    public String askQuestion(String question) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            String requestBody = """
                {
                  "model": "llama3-70b-8192",
                  "messages": [{"role": "user", "content": "%s"}]
                }
                """.formatted(question);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            ObjectMapper mapper = new ObjectMapper();
            var responseJson = mapper.readTree(connection.getInputStream());

            String answer = responseJson.path("choices").get(0).path("message").path("content").asText();

            return answer.isEmpty() ? "Pas de réponse du modèle." : answer.trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur API: " + e.getMessage();
        }
    }
}
