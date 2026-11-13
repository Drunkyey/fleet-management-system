package fleetManagement.presentpack.controller;

import fleetManagement.presentpack.model.VehicleAssignment;
import fleetManagement.presentpack.repository.VehicleAssignmentRepository;
import fleetManagement.presentpack.service.VehicleAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate; // <-- IMPORT NOU
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "http://localhost:8080")
public class VehicleAssignmentController {

    @Autowired
    private VehicleAssignmentRepository repository;

    @Autowired
    private VehicleAssignmentService service;

    // Obiectul care trimite mesaje prin WebSocket
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/active")
    public List<VehicleAssignment> getActiveAssignments() {
        return service.getActiveAssignments();
    }

    @GetMapping
    public List<VehicleAssignment> getAllAssignments() {
        return repository.findAll();
    }

    @PostMapping
    public org.springframework.http.ResponseEntity<?> createAssignment(@RequestBody VehicleAssignment assignment) {

        // Dacă e o alocare nouă (fără dată de predare definită), verificăm regulile
        if (assignment.getReturnDate() == null) {

            // 1. Mașina e deja ocupată?
            if (repository.existsByVehicleIdAndReturnDateIsNull(assignment.getVehicle().getId())) {
                return org.springframework.http.ResponseEntity.badRequest()
                        .body("Eroare: Acest vehicul este deja alocat și se află pe traseu!");
            }

            // 2. Șoferul e deja pe altă mașină?
            if (repository.existsByDriverIdAndReturnDateIsNull(assignment.getDriver().getId())) {
                return org.springframework.http.ResponseEntity.badRequest()
                        .body("Eroare: Acest șofer are deja o alocare activă!");
            }
        }

        VehicleAssignment saved = repository.save(assignment);
        messagingTemplate.convertAndSend("/topic/assignments", "Data a fost actualizata!");
        return org.springframework.http.ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public VehicleAssignment updateAssignment(@PathVariable Integer id, @RequestBody VehicleAssignment assignment) {
        assignment.setId(id);
        VehicleAssignment saved = repository.save(assignment);
        messagingTemplate.convertAndSend("/topic/assignments", "Data a fost actualizata!");
        return saved;
    }

    @DeleteMapping("/{id}")
    public void deleteAssignment(@PathVariable Integer id) {
        repository.deleteById(id);
        messagingTemplate.convertAndSend("/topic/assignments", "Data a fost actualizata!");
    }
}