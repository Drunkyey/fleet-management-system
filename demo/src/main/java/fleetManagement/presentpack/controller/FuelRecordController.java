package fleetManagement.presentpack.controller;

import fleetManagement.presentpack.model.FuelRecord;
import fleetManagement.presentpack.repository.FuelRecordRepository;
import fleetManagement.presentpack.service.FuelRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fuel")
@CrossOrigin(origins = "http://localhost:8080")
public class FuelRecordController {

    @Autowired
    private FuelRecordRepository fuelRecordRepository;

    @Autowired
    private FuelRecordService fuelRecordService;

    // --- FUNCȚIA COMPLEXĂ (KPI Dashboard) ---
    @GetMapping("/average-cost")
    public Double getAverageCost() {
        return fuelRecordService.calculateAverageFuelCostPerLiter();
    }

    // --- OPERAȚIILE CRUD ---
    @GetMapping
    public List<FuelRecord> getAllFuelRecords() {
        return fuelRecordRepository.findAll();
    }

    @PostMapping
    public FuelRecord createFuelRecord(@RequestBody FuelRecord fuelRecord) {
        return fuelRecordRepository.save(fuelRecord);
    }

    // --- ADAUGĂM METODA PUT PENTRU EDITARE ---
    @PutMapping("/{id}")
    public FuelRecord updateFuelRecord(@PathVariable Integer id, @RequestBody FuelRecord fuelRecord) {
        fuelRecord.setId(id);
        return fuelRecordRepository.save(fuelRecord);
    }

    @DeleteMapping("/{id}")
    public void deleteFuelRecord(@PathVariable Integer id) {
        fuelRecordRepository.deleteById(id);
    }
}