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
        Optional<Medico> medicoExistente = medicoRepository.findByDni(medico.getDni());
        if (medicoExistente.isPresent()){
            return false;
        }

        medicoRepository.save(medico);
        return true;
    }
    public boolean bajaMedico(Medico medico){
        Optional<Medico> medicoExistente = medicoRepository.findByDni(medico.getDni());
        if (medicoExistente.isPresent()){
            medicoRepository.delete(medico);
            return true;
        }
        return false;
    }

    public List<Medico> getMedicos() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> getMedico(String dni) {
        return medicoRepository.findByDni(dni);
    }
    public Optional<Medico> getMedico(Long id) {
        return medicoRepository.findById(id);
    }

}
