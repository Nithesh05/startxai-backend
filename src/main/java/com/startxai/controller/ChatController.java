package com.startxai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.startxai.ApiService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ApiService apiService;

    @PostMapping("/send")
    public Map<String, String> sendMessage(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String reply = apiService.getAIResponse(message);
        return Map.of("reply", reply);
    }
}
