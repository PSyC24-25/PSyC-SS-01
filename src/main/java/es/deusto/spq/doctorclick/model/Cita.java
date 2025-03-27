package es.deusto.spq.doctorclick.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private Especialidad especialidad;

    public Cita() {}

    public Cita(Paciente paciente, Medico medico, LocalDateTime fecha, Especialidad especialidad) {
        this.paciente = paciente;
        this.medico = medico;
        this.fecha = fecha;
        this.especialidad = especialidad;
    }

    public long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void setMedico(Medico medico) {
        if (medico.getEspecialidad().equals(this.especialidad)) {
            this.medico = medico;
        } else {
            throw new IllegalArgumentException("El médico no tiene la especialidad adecuada.");
        }

    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }
}
