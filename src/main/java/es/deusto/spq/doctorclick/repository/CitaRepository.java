package es.deusto.spq.doctorclick.repository;

import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPaciente(Paciente paciente);
    List<Cita> findByMedico_Dni(String medicoDni);
    Optional<Cita> findById(long id);
}
