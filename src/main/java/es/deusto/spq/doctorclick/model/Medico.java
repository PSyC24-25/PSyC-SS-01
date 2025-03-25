package es.deusto.spq.doctorclick.model;

import jakarta.persistence.*;

@Entity
public class Medico extends Usuario {

    private Especialidad especialidad;

    public Especialidad getEspecialidad() {
        return especialidad;
    }
    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }
}

