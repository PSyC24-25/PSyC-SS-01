package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.repository.CitaRepository;
import es.deusto.spq.doctorclick.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;

    @Autowired
    public MedicoService(MedicoRepository medicoRepository, CitaRepository citaRepository) {
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
    }

    public boolean registrarMedico(Medico medico){
        Medico user = medicoRepository.findByDni( medico.getDni() );
        if ( user != null ){
            return false;
        }
        medicoRepository.save(medico);
        return true;
    }

    public List<Medico> getMedicos() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> getMedico(Long id) {
        return medicoRepository.findById(id);
    }

    public List<Cita> getCitas(String dni){
        return citaRepository.findByMedico_Dni(dni);
    }
    public Optional<Cita> getCita(Long id){
        return citaRepository.findById(id);
    }
}
