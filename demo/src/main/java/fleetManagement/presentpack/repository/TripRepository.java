package fleetManagement.presentpack.repository;

import fleetManagement.presentpack.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {

    // CUSTOM QUERY 2: Calculează suma kilometrilor parcurși de o anumită mașină
    @Query("SELECT SUM(t.distanceKm) FROM Trip t WHERE t.vehicle.id = :vehicleId")
    Double calculateTotalDistanceForVehicle(@Param("vehicleId") Integer vehicleId);
}