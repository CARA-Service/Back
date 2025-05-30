package com.syu.cara.recommendation.openai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class OpenAIClient {

    @Value("${SPRING_AI_OPENAI_API_KEY}")
    private String apiKey;

    public String chat(String prompt) {
        HttpClient client = HttpClient.newHttpClient();
        String body = String.format("""
        {
            "model": "gpt-3.5-turbo",
            "messages": [{"role": "user", "content": "%s"}]
        }
        """, prompt.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("GPT 호출 실패", e);
        }
    }
}
