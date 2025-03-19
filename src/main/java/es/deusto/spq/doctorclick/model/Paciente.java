package es.deusto.spq.doctorclick.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

@Entity
@Table(name = "Pacientes")
public class Paciente {
    @Id
    @GeneratedValue
    private Long idPaciente;
    private String nombrePaciente;
    private String apellidoPaciente;
    private String dniPaciente;

}
