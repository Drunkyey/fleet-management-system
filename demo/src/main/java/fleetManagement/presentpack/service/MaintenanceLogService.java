package fleetManagement.presentpack.service;

import fleetManagement.presentpack.model.MaintenanceLog;
import fleetManagement.presentpack.repository.MaintenanceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceLogService {

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    // CREATE
    public MaintenanceLog saveLog(MaintenanceLog log) {
        return maintenanceLogRepository.save(log);
    }

    // READ ALL
    public List<MaintenanceLog> getAllLogs() {
        return maintenanceLogRepository.findAll();
    }

    // READ BY ID
    public Optional<MaintenanceLog> getLogById(Integer id) {
        return maintenanceLogRepository.findById(id);
    }

    // UPDATE
    public MaintenanceLog updateLog(Integer id, MaintenanceLog updatedLog) {
        if (maintenanceLogRepository.existsById(id)) {
            updatedLog.setId(id);
            return maintenanceLogRepository.save(updatedLog);
        }
        return null;
    }

    // DELETE
    public void deleteLog(Integer id) {
        maintenanceLogRepository.deleteById(id);
    }
}