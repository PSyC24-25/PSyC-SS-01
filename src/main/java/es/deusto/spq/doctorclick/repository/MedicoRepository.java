package es.deusto.spq.doctorclick.repository;

import es.deusto.spq.doctorclick.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
}
