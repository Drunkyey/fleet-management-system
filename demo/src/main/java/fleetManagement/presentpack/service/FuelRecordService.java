package fleetManagement.presentpack.service;

import fleetManagement.presentpack.model.FuelRecord;
import fleetManagement.presentpack.repository.FuelRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FuelRecordService {

    @Autowired
    private FuelRecordRepository repository;

    // OPERAȚIE COMPLEXĂ 3: Calculează costul mediu pe litru de carburant pentru toată flota
    public Double calculateAverageFuelCostPerLiter() {
        List<FuelRecord> records = repository.findAll();
        if (records.isEmpty()) return 0.0;

        double totalLiters = 0;
        double totalCost = 0;

        for (FuelRecord record : records) {
            if (record.getLitersFilled() != null && record.getTotalCost() != null) {
                totalLiters += record.getLitersFilled();
                totalCost += record.getTotalCost();
            }
        }

        return totalLiters > 0 ? (totalCost / totalLiters) : 0.0;
    }
}