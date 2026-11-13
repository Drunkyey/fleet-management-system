package fleetManagement.presentpack.service;

import fleetManagement.presentpack.model.InsurancePolicy;
import fleetManagement.presentpack.repository.InsurancePolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InsurancePolicyService {

    @Autowired
    private InsurancePolicyRepository repository;

    // OPERAȚIE COMPLEXĂ 1: Raport cu asigurările care expiră în următoarele 30 de zile
    public List<InsurancePolicy> getExpiringPoliciesInNext30Days() {
        LocalDate today = LocalDate.now();
        LocalDate next30Days = today.plusDays(30);
        return repository.findPoliciesExpiringBetween(today, next30Days);
    }

    // + Poți adăuga metodele clasice save, findAll, delete aici dacă vrei
}