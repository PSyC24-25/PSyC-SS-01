package es.deusto.spq.doctorclick.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

@Entity
@Table(name = "Medicos")
public class Medico {
    @Id
    @GeneratedValue
    private Long idMedico;
    private String nombreMedico;
    private String apellidoMedico;
    private String dniMedico;
    private String especialidad;

}
