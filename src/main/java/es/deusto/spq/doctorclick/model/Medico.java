package es.deusto.spq.doctorclick.model;

import jakarta.persistence.*;

@Entity
public class Medico extends Usuario {
    private Especialidad especialidad;
}

