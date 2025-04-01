package es.deusto.spq.doctorclick.model;

import jakarta.persistence.*;
import jakarta.validation.Constraint;

@Entity
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, nullable = false)
    private String dni;

    private String nombre;
    private String apellidos;

    private String contrasenia;

    public Usuario() {}
    public Usuario(String dni, String nombre, String apellidos, String contrasenia) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.contrasenia = contrasenia;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public long getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getContrasenia() {
        return contrasenia;
    }
}
