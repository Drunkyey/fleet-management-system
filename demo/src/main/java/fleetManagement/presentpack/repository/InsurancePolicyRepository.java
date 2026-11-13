package fleetManagement.presentpack.repository;

import fleetManagement.presentpack.model.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Integer> {

    // CUSTOM QUERY 1: Găsește toate polițele care expiră într-un anumit interval de timp
    @Query("SELECT i FROM InsurancePolicy i WHERE i.endDate BETWEEN :startDate AND :endDate")
    List<InsurancePolicy> findPoliciesExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}