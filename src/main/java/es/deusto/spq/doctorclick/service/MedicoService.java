package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {
    private final MedicoRepository medicoRepository;

    @Autowired
    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
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
}
