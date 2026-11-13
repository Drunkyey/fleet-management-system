package fleetManagement.presentpack.controller;

import fleetManagement.presentpack.model.Vehicle;
import fleetManagement.presentpack.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public List<Vehicle> getAll() { return vehicleService.getAllVehicles(); }

    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle) { return vehicleService.saveVehicle(vehicle); }

    @PutMapping("/{id}")
    public Vehicle update(@PathVariable Integer id, @RequestBody Vehicle vehicle) { return vehicleService.updateVehicle(id, vehicle); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { vehicleService.deleteVehicle(id); }
}