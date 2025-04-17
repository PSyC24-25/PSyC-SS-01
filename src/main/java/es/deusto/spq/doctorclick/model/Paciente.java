package es.deusto.spq.doctorclick.model;

import jakarta.persistence.*;

@Entity
public class Paciente extends Usuario {

    public Paciente() {
        super();
    }
    public Paciente(String dni, String nombre, String apellidos, String contrasenia) {
        super(dni, nombre, apellidos, contrasenia);
    }
}
