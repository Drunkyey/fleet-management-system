package fleetManagement.presentpack.service;

import fleetManagement.presentpack.model.InsurancePolicy;
import fleetManagement.presentpack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleAssignmentRepository assignmentRepository;

    @Autowired
    private InsurancePolicyRepository insurancePolicyRepository;

    private final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public String askFleetAssistant(String userQuestion) {
        // 1. Extragem datele reale din baza de date
        long totalVehicles = vehicleRepository.count();
        long totalDrivers = driverRepository.count();
        long activeAssignments = assignmentRepository.findAll().stream()
                .filter(a -> a.getReturnDate() == null)
                .count();

        List<InsurancePolicy> policies = insurancePolicyRepository.findAll();
        String insuranceSummary = policies.isEmpty() ? "Nicio polita de asigurare inregistrata." :
                policies.stream()
                        .map(p -> "Vehicul " + (p.getVehicle() != null ? p.getVehicle().getLicensePlate() : "N/A") +
                                " (Expira: " + p.getEndDate() + ", Cost: " + p.getCost() + " RON)")
                        .collect(Collectors.joining("; "));

        // 2. Construim prompt-ul
        String systemPrompt = "Esti un asistent inteligent pentru managementul unei flote auto. " +
                "Raspunde scurt, profesionist si direct in limba romana, bazandu-te EXCLUSIV pe datele de mai jos:\n" +
                "- Total vehicule in flota: " + totalVehicles + "\n" +
                "- Total soferi inregistrati: " + totalDrivers + "\n" +
                "- Vehicule aflate acum pe traseu (alocari active): " + activeAssignments + "\n" +
                "- Situatie polite asigurare: " + insuranceSummary + "\n\n" +
                "Intrebarea utilizatorului: " + userQuestion;

        try {
            // Construim JSON-ul manual cu escape la caractere speciale
            String escapedPrompt = escapeJson(systemPrompt);
            String jsonBody = "{\"model\":\"llama3.2\",\"prompt\":\"" + escapedPrompt + "\",\"stream\":false}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return extractResponseFromJson(response.body());
        } catch (Exception e) {
            return "Eroare la conectarea cu Ollama (verifica daca ruleaza local pe portul 11434): " + e.getMessage();
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String extractResponseFromJson(String json) {
        Matcher matcher = Pattern.compile("\"response\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(json);
        if (matcher.find()) {
            return matcher.group(1)
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }
        return "Nu am primit un raspuns valid de la Ollama: " + json;
    }
}