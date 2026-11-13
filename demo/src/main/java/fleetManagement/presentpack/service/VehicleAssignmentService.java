package fleetManagement.presentpack.service;

import fleetManagement.presentpack.model.VehicleAssignment;
import fleetManagement.presentpack.repository.VehicleAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleAssignmentService {

    @Autowired
    private VehicleAssignmentRepository repository;

    // OPERAȚIE COMPLEXĂ 5: Găsește toate asocierile active în acest moment
    // (Mașini care sunt pe traseu acum, unde data de returnare este în viitor sau null)
    public List<VehicleAssignment> getActiveAssignments() {
        LocalDate today = LocalDate.now();
        List<VehicleAssignment> allAssignments = repository.findAll();

        return allAssignments.stream()
                .filter(assignment ->
                        assignment.getAssignDate() != null &&
                                !assignment.getAssignDate().isAfter(today) &&
                                (assignment.getReturnDate() == null || assignment.getReturnDate().isAfter(today))
                )
                .collect(Collectors.toList());
    }
}