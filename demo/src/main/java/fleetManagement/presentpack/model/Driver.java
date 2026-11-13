package fleetManagement.presentpack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String lastName;
    private String licenseNumber;
    private String email;
    private String phone;
    private LocalDate hireDate;

    // Relație: Un șofer poate avea mai multe alocări de mașini în timp
    @JsonIgnore
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<VehicleAssignment> assignments;
}