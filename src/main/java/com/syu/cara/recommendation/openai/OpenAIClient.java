package com.syu.cara.recommendation.openai;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class OpenAIClient {

    @Value("${SPRING_AI_OPENAI_API_KEY}")
    private String apiKey;

    @PostConstruct
    public void init() {
        System.out.println("🔥 OpenAI Key = " + apiKey);
    }

//    public String chat(String prompt) {
//        HttpClient client = HttpClient.newHttpClient();
//        String body = String.format("""
//        {
//            "model": "gpt-3.5-turbo",
//            "messages": [{"role": "user", "content": "%s"}]
//        }
//        """, prompt.replace("\"", "\\\""));
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
//                .header("Authorization", "Bearer " + apiKey)
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(body))
//                .build();
//
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            return response.body();
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException("GPT 호출 실패", e);
//        }
//    }
    public String chat(String prompt) {
        HttpClient client = HttpClient.newHttpClient();

        // JSON 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode bodyNode = objectMapper.createObjectNode();
        bodyNode.put("model", "gpt-3.5-turbo");
        bodyNode.putArray("messages").addObject().put("role", "user").put("content", prompt);

        // JSON을 문자열로 변환
        String body = bodyNode.toString();

        // HTTP 요청 준비
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
