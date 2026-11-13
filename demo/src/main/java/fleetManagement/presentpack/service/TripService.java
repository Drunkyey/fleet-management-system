package fleetManagement.presentpack.service;

import fleetManagement.presentpack.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripService {

    @Autowired
    private TripRepository repository;

    // OPERAȚIE COMPLEXĂ 2: Obține kilometrajul total rulat de un vehicul în firmă
    public Double getTotalDistanceForVehicle(Integer vehicleId) {
        Double totalDistance = repository.calculateTotalDistanceForVehicle(vehicleId);
        // Dacă mașina nu are nicio cursă, query-ul returnează null, așa că dăm noi 0.0
        return totalDistance != null ? totalDistance : 0.0;
    }
}