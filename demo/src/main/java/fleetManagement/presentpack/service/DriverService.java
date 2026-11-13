package fleetManagement.presentpack.service;

import fleetManagement.presentpack.model.Driver;
import fleetManagement.presentpack.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DriverService {

    @Autowired
    private DriverRepository repository;

    // OPERAȚIE COMPLEXĂ 4: Obține vechimea medie (în ani) a șoferilor din firmă
    public Double getAverageDriverExperienceYears() {
        List<Driver> drivers = repository.findAll();
        if (drivers.isEmpty()) return 0.0;

        int currentYear = LocalDate.now().getYear();
        double totalYears = 0;

        for (Driver driver : drivers) {
            if (driver.getHireDate() != null) {
                totalYears += (currentYear - driver.getHireDate().getYear());
            }
        }
        return totalYears / drivers.size();
    }
}