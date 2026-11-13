package fleetManagement.presentpack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Poți apela acest endpoint (ex: din Postman) ca să trimiți o alertă pe ecran în Vue/Android
    @PostMapping("/api/notify")
    public String sendNotification(@RequestParam String message) {
        // Trimite mesajul către toți cei abonați la /topic/alerts
        messagingTemplate.convertAndSend("/topic/alerts", message);
        return "Notificare trimisă cu succes!";
    }
}