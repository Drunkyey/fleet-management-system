package fleetManagement.presentpack.controller;

import fleetManagement.presentpack.model.InsurancePolicy;
import fleetManagement.presentpack.repository.InsurancePolicyRepository;
import fleetManagement.presentpack.service.InsurancePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance")
@CrossOrigin(origins = "http://localhost:8080")
public class InsurancePolicyController {

    @Autowired
    private InsurancePolicyRepository repository;

    @Autowired
    private InsurancePolicyService service;

    // --- FUNCȚIA COMPLEXĂ ---
    @GetMapping("/expiring-soon")
    public List<InsurancePolicy> getExpiringSoon() {
        return service.getExpiringPoliciesInNext30Days();
    }

    // --- OPERAȚIILE CRUD ---
    @GetMapping
    public List<InsurancePolicy> getAllPolicies() {
        return repository.findAll();
    }

    @PostMapping
    public InsurancePolicy createPolicy(@RequestBody InsurancePolicy policy) {
        return repository.save(policy);
    }

    @PutMapping("/{id}")
    public InsurancePolicy updatePolicy(@PathVariable Integer id, @RequestBody InsurancePolicy policy) {
        policy.setId(id);
        return repository.save(policy);
    }

    @DeleteMapping("/{id}")
    public void deletePolicy(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}