package fleetManagement.presentpack.controller;

import fleetManagement.presentpack.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:8080")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/ask")
    public Map<String, String> askAi(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");
        String answer = aiService.askFleetAssistant(question);
        return Map.of("answer", answer);
    }
}