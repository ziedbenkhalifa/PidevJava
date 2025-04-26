package tn.cinema.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Maps;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class DialogflowService {
    private static final String PROJECT_ID = "my-project-4489-1741031296470";
    private static final String CREDENTIALS_FILE = "/my-project-4489-1741031296470-f405a23c74d6.json";
    private static final String LANGUAGE_CODE = "fr-FR";

    private final SessionsClient sessionsClient;
    private final Map<String, SessionName> sessions = Maps.newConcurrentMap();

    public DialogflowService() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new FileInputStream(CREDENTIALS_FILE));

        SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        this.sessionsClient = SessionsClient.create(sessionsSettings);
    }

    public String detectIntent(String sessionId, String text) throws Exception {
        SessionName session = sessions.computeIfAbsent(
                sessionId,
                id -> SessionName.of(PROJECT_ID, id));

        TextInput.Builder textInput = TextInput.newBuilder()
                .setText(text)
                .setLanguageCode(LANGUAGE_CODE);

        QueryInput queryInput = QueryInput.newBuilder()
                .setText(textInput)
                .build();

        DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
        QueryResult queryResult = response.getQueryResult();

        return queryResult.getFulfillmentText();
    }

    public void close() {
        if (sessionsClient != null) {
            sessionsClient.close();
        }
    }
}