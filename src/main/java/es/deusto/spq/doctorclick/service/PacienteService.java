package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public boolean registrarPaciente(Paciente paciente){
        Paciente user = pacienteRepository.findByDni(paciente.getDni());
        if(user != null){
            return false;
        }
        pacienteRepository.save(paciente);
        return true;
    }
}
