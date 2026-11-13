package fleetManagement.presentpack.controller;

import fleetManagement.presentpack.model.Trip;
import fleetManagement.presentpack.repository.TripRepository;
import fleetManagement.presentpack.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "http://localhost:8080")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripService tripService;

    // --- FUNCȚIA COMPLEXĂ ---
    @GetMapping("/vehicle/{vehicleId}/total-distance")
    public Double getTotalDistance(@PathVariable Integer vehicleId) {
        return tripService.getTotalDistanceForVehicle(vehicleId);
    }

    // --- OPERAȚIILE CRUD ---
    @GetMapping
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @PostMapping
    public Trip createTrip(@RequestBody Trip trip) {
        return tripRepository.save(trip);
    }

    @PutMapping("/{id}")
    public Trip updateTrip(@PathVariable Integer id, @RequestBody Trip trip) {
        trip.setId(id);
        return tripRepository.save(trip);
    }

    @DeleteMapping("/{id}")
    public void deleteTrip(@PathVariable Integer id) {
        tripRepository.deleteById(id);
    }
}