package fleetManagement.presentpack.controller;

import fleetManagement.presentpack.model.MaintenanceLog;
import fleetManagement.presentpack.service.MaintenanceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceLogController {

    @Autowired
    private MaintenanceLogService maintenanceService;

    @GetMapping
    public List<MaintenanceLog> getAll() { return maintenanceService.getAllLogs(); }

    @PostMapping
    public MaintenanceLog create(@RequestBody MaintenanceLog log) { return maintenanceService.saveLog(log); }

    @PutMapping("/{id}")
    public MaintenanceLog update(@PathVariable Integer id, @RequestBody MaintenanceLog log) { return maintenanceService.updateLog(id, log); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { maintenanceService.deleteLog(id); }
}