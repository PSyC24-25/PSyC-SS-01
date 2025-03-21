package es.deusto.spq.doctorclick.repository;

import es.deusto.spq.doctorclick.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Paciente findByDni(String dni);
}
