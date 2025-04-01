package es.deusto.spq.doctorclick.repository;

import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPaciente(Paciente paciente);
}
