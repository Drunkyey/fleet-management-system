package fleetManagement.presentpack.controller;

import fleetManagement.presentpack.model.Fleet;
import fleetManagement.presentpack.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fleets")
public class FleetController {

    @Autowired
    private FleetService fleetService;

    @GetMapping
    public List<Fleet> getAll() { return fleetService.getAllFleets(); }

    @PostMapping
    public Fleet create(@RequestBody Fleet fleet) { return fleetService.saveFleet(fleet); }

    @PutMapping("/{id}")
    public Fleet update(@PathVariable Integer id, @RequestBody Fleet fleet) { return fleetService.updateFleet(id, fleet); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { fleetService.deleteFleet(id); }
}