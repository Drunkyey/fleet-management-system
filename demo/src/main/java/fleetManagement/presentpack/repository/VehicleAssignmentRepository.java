package fleetManagement.presentpack.repository;

import fleetManagement.presentpack.model.VehicleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleAssignmentRepository extends JpaRepository<VehicleAssignment, Integer> {

    // Verifică dacă mașina este deja pe traseu
    boolean existsByVehicleIdAndReturnDateIsNull(Integer vehicleId);

    // Verifică dacă șoferul are deja o mașină
    boolean existsByDriverIdAndReturnDateIsNull(Integer driverId);
}