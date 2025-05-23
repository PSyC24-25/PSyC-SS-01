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

    List<Cita> findByMedicoDni(String medicoDni);
    Optional<Cita> findByIdAndPacienteDni(Long id, String dni);
    void deleteById(Long id);

    List<Cita> findByPacienteDniAndFechaBefore(String dni, LocalDateTime fecha);
    List<Cita> findByPacienteDniAndFechaAfter(String dni, LocalDateTime fecha);

    List<Cita> findByMedicoDniAndFechaBefore(String dni, LocalDateTime fecha);

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
