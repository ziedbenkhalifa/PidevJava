package tn.cinema.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class FacePlusPlusService {
    private final OkHttpClient client;
    private final String apiKey;
    private final String apiSecret;
    private final Gson gson;
    private static final String FACEPP_BASE_URL = "https://api-us.faceplusplus.com/facepp/v3";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000; // 1 second

    public FacePlusPlusService(String apiKey, String apiSecret) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(50, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(50, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.gson = new Gson();
    }

    public JsonObject detectFace(String photoPath) throws IOException {
        File file = new File(photoPath);
        if (!file.exists()) {
            throw new IOException("Photo file does not exist: " + photoPath);
        }

        RequestBody detectBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", apiKey)
                .addFormDataPart("api_secret", apiSecret)
                .addFormDataPart("image_file", file.getName(),
                        RequestBody.create(file, MediaType.parse("image/*")))
                .build();

        return executeWithRetry(FACEPP_BASE_URL + "/detect", detectBody);
    }

    public JsonObject createFaceSet(String outerId) throws IOException {
        RequestBody createBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", apiKey)
                .addFormDataPart("api_secret", apiSecret)
                .addFormDataPart("outer_id", outerId)
                .build();

        JsonObject result = executeWithRetry(FACEPP_BASE_URL + "/faceset/create", createBody);
        if (result.has("error_message") && result.get("error_message").getAsString().contains("FACESET_EXIST")) {
            // Ignore if the FaceSet already exists
            return result;
        }
        return result;
    }

    public JsonObject addFaceToSet(String outerId, String faceToken) throws IOException {
        RequestBody addBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", apiKey)
                .addFormDataPart("api_secret", apiSecret)
                .addFormDataPart("outer_id", outerId)
                .addFormDataPart("face_tokens", faceToken)
                .build();

        return executeWithRetry(FACEPP_BASE_URL + "/faceset/addface", addBody);
    }

    public JsonObject searchFace(String outerId, String faceToken) throws IOException {
        RequestBody searchBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", apiKey)
                .addFormDataPart("api_secret", apiSecret)
                .addFormDataPart("outer_id", outerId)
                .addFormDataPart("face_token", faceToken)
                .build();

        return executeWithRetry(FACEPP_BASE_URL + "/search", searchBody);
    }

    private JsonObject executeWithRetry(String url, RequestBody body) throws IOException {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String responseBody = response.body().string();
                        if (responseBody.contains("CONCURRENCY_LIMIT_EXCEEDED")) {
                            retryCount++;
                            if (retryCount == MAX_RETRIES) {
                                throw new IOException("Failed to execute request after " + MAX_RETRIES + " attempts: CONCURRENCY_LIMIT_EXCEEDED");
                            }
                            try {
                                Thread.sleep(RETRY_DELAY_MS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new IOException("Interrupted during retry delay", e);
                            }
                            continue;
                        }
                        throw new IOException("Request failed. HTTP Status: " + response.code() + ", Response: " + responseBody);
                    }

                    String responseBody = response.body().string();
                    return gson.fromJson(responseBody, JsonObject.class);
                }
            } catch (IOException e) {
                if (e.getMessage().contains("CONCURRENCY_LIMIT_EXCEEDED")) {
                    retryCount++;
                    if (retryCount == MAX_RETRIES) {
                        throw new IOException("Failed to execute request after " + MAX_RETRIES + " attempts: CONCURRENCY_LIMIT_EXCEEDED", e);
                    }
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted during retry delay", ie);
                    }
                } else {
                    throw e;
                }
            }
        }
        throw new IOException("Failed to execute request after " + MAX_RETRIES + " attempts");
    }
}