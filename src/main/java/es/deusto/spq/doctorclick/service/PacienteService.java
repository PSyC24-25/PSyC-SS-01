package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public boolean registrarPaciente(Paciente paciente){
        Optional<Paciente> pacienteExistente = pacienteRepository.findByDni(paciente.getDni());
        if(pacienteExistente.isPresent()){
            return false;
        }

        pacienteRepository.save(paciente);
        return true;
    }

    public boolean bajaPaciente(Paciente paciente){
        Optional<Paciente> pacienteExistente = pacienteRepository.findByDni(paciente.getDni());
        if (pacienteExistente.isPresent()){
            pacienteRepository.delete(paciente);
            return true;
        }
        return false;
    }

    public Optional<Paciente> getPaciente(String dni) {
        return pacienteRepository.findByDni(dni);
    }
    public Optional<Paciente> getPaciente(Long id) {
        return pacienteRepository.findById(id);
    }
}
