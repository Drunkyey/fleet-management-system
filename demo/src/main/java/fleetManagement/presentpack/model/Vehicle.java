package fleetManagement.presentpack.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String vin;
    private String brand;
    private String model;
    private String licensePlate;
    private Integer manufactureYear;

    // Relația cu Flota (Many-to-One)
    @ManyToOne
    @JoinColumn(name = "fleet_id", nullable = false)
    private Fleet fleet;

    // --- LEGĂTURILE CĂTRE CELELALTE TABELE (One-to-Many) ---
    // cascade = CascadeType.ALL asigură integritatea bazei de date

    @JsonIgnore
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<MaintenanceLog> maintenanceLogs;

    @JsonIgnore
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<FuelRecord> fuelRecords;

    @JsonIgnore
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<Trip> trips;

    @JsonIgnore
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<InsurancePolicy> insurancePolicies;

    @JsonIgnore
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<VehicleAssignment> assignments;
}