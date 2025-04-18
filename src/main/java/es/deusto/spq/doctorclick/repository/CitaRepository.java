package es.deusto.spq.doctorclick.repository;

import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPaciente(Paciente paciente);
    List<Cita> findByMedico_Dni(String medicoDni);
    Optional<Cita> findById(long id);

    @Query("SELECT c FROM Cita c " +
            "WHERE c.medico.id = :id " +
            "AND c.fecha >= :startOfDay " +
            "AND c.fecha < :startOfNextDay")
    List<Cita> findByMedicoDniAndFechaInDay(
            @Param("id") Long id,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay
    );
}
