package com.startxai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.json.JSONObject;

@Service
public class ApiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private boolean strategyGenerated = false;
    private String lastBusiness = "";
    private String lastBudget = "";
    private String lastLocation = "";

    public String getAIResponse(String userMessage) {
        try {
            // ✅ Reset option
            if (userMessage.equalsIgnoreCase("reset")) {
                strategyGenerated = false;
                lastBusiness = "";
                lastBudget = "";
                lastLocation = "";
                return "Chat has been reset 🔄 Let's start a new business plan! What type of business are you planning?";
            }

            // ✅ Detect if frontend sent strategy generation prompt
            if (userMessage.startsWith("You are an expert AI business strategist")) {
                strategyGenerated = true;

                // Extract info for memory
                lastBusiness = extractValue(userMessage, "Business:");
                lastBudget = extractValue(userMessage, "Budget:");
                lastLocation = extractValue(userMessage, "Location:");

                String strategy = callGeminiAPI(userMessage);
                return "📊 Here’s your personalized business strategy:\n\n" + strategy
                        + "\n\n💬 You can now ask me anything related to your business strategy — like marketing, finance, or local growth opportunities!";
            }

            // ✅ After strategy — use memory for chat
            if (strategyGenerated) {
                String context = buildContext();
                String prompt = "You are StartXAIChatBot, a smart business strategist AI. "
                        + "Use the following context for every response:\n"
                        + context + "\n\nUser said: " + userMessage;
                return callGeminiAPI(prompt);
            }

            // ✅ Default fallback
            return "👋 Hi! Let's start fresh. What type of business are you planning?";

        } catch (Exception e) {
            return "⚠️ Error: " + e.getMessage();
        }
    }

    // 🔹 Build memory context for future responses
    private String buildContext() {
        StringBuilder context = new StringBuilder();
        if (!lastBusiness.isEmpty()) context.append("Business: ").append(lastBusiness).append(". ");
        if (!lastBudget.isEmpty()) context.append("Budget: ").append(lastBudget).append(". ");
        if (!lastLocation.isEmpty()) context.append("Location: ").append(lastLocation).append(". ");
        context.append("Use this information to provide relevant and location-aware business advice.");
        return context.toString();
    }

    // 🔹 Extract key info like "Business:", "Budget:", "Location:"
    private String extractValue(String text, String key) {
        try {
            int start = text.indexOf(key);
            if (start == -1) return "";
            int end = text.indexOf("\n", start);
            if (end == -1) end = text.length();
            return text.substring(start + key.length(), end).trim();
        } catch (Exception e) {
            return "";
        }
    }

    // 🔹 Send to Gemini API
    private String callGeminiAPI(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            JSONObject content = new JSONObject()
                    .put("contents", new org.json.JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new org.json.JSONArray()
                                            .put(new JSONObject().put("text", prompt)))));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            HttpEntity<String> request = new HttpEntity<>(content.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                return json.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");
            } else {
                return "Gemini API Error: " + response.getStatusCode();
            }
        } catch (Exception e) {
            return "Failed to connect to Gemini API: " + e.getMessage();
        }
    }
}
