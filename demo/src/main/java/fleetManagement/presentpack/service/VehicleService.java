package fleetManagement.presentpack.service;

import fleetManagement.presentpack.model.Vehicle;
import fleetManagement.presentpack.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Integer id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle updateVehicle(Integer id, Vehicle updatedVehicle) {
        if (vehicleRepository.existsById(id)) {
            updatedVehicle.setId(id);
            return vehicleRepository.save(updatedVehicle);
        }
        return null;
    }

    public void deleteVehicle(Integer id) {
        vehicleRepository.deleteById(id);
    }
}