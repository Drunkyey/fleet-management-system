package fleetManagement.presentpack.service;

import fleetManagement.presentpack.model.Fleet;
import fleetManagement.presentpack.repository.FleetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FleetService {

    @Autowired
    private FleetRepository fleetRepository;

    // CREATE
    public Fleet saveFleet(Fleet fleet) {
        return fleetRepository.save(fleet);
    }

    // READ (Get all)
    public List<Fleet> getAllFleets() {
        return fleetRepository.findAll();
    }

    // READ (Get by ID)
    public Optional<Fleet> getFleetById(Integer id) {
        return fleetRepository.findById(id);
    }

    // UPDATE
    public Fleet updateFleet(Integer id, Fleet updatedFleet) {
        if (fleetRepository.existsById(id)) {
            updatedFleet.setId(id); // Ne asigurăm că suprascriem înregistrarea corectă
            return fleetRepository.save(updatedFleet);
        }
        return null; // În mod ideal aici aruncăm o excepție, dar păstrăm simplu deocamdată
    }

    // DELETE
    public void deleteFleet(Integer id) {
        fleetRepository.deleteById(id);
    }
}