package fleetManagement.presentpack.repository;

import fleetManagement.presentpack.model.Fleet; // Asigură-te că importul corespunde cu pachetul unde ai pus clasa Fleet
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FleetRepository extends JpaRepository<Fleet, Integer> {
    // JpaRepository ia doi parametri: <Tipul Entității, Tipul ID-ului>
}