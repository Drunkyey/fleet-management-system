package fleetManagement.presentpack.controller;

import fleetManagement.presentpack.model.Driver;
import fleetManagement.presentpack.repository.DriverRepository;
import fleetManagement.presentpack.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:8080")
public class DriverController {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverService driverService;

    // --- FUNCȚIA COMPLEXĂ (KPI Dashboard) ---
    @GetMapping("/average-experience")
    public Double getAverageExperience() {
        return driverService.getAverageDriverExperienceYears();
    }

    // --- OPERAȚIILE CRUD ---
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @PostMapping
    public Driver createDriver(@RequestBody Driver driver) {
        return driverRepository.save(driver);
    }

    @DeleteMapping("/{id}")
    public void deleteDriver(@PathVariable Integer id) {
        driverRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Driver updateDriver(@PathVariable Integer id, @RequestBody Driver driver) {
        driver.setId(id);
        return driverRepository.save(driver);
    }
}