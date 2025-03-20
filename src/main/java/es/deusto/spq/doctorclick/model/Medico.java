package es.deusto.spq.doctorclick.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedico;

    private String nombre;
    private String apellido;
    private String dni;
    private Especialidad especialidad;

}
