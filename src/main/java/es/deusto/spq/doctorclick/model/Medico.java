package es.deusto.spq.doctorclick.model;

import jakarta.persistence.*;

@Entity
public class Medico extends Usuario {
    private Especialidad especialidad;

    public Medico() {
        super();
    }
    public Medico(String dni, String nombre, String apellidos, String contrasenia, Especialidad especialidad) {
        super(dni, nombre, apellidos, contrasenia);
        this.especialidad = especialidad;
    }
    public Medico(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }
    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }
 
}

