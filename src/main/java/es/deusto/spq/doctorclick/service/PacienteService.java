package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Especialidad;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.repository.CitaRepository;
import es.deusto.spq.doctorclick.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;
    private final MedicoService medicoService;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository, CitaRepository citaRepository, MedicoService medicoService) {
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
        this.medicoService = medicoService;
    }

    public boolean registrarPaciente(Paciente paciente){
        Optional<Paciente> pacienteExistente = pacienteRepository.findByDni(paciente.getDni());
        if(pacienteExistente.isPresent()){
            return false;
        }

        pacienteRepository.save(paciente);
        return true;
    }

    public List<Cita> obtenerCitasPorDni(String dni) {
        Optional<Paciente> paciente = pacienteRepository.findByDni(dni);
        if(paciente.isEmpty()){
            return new ArrayList<>();
        }

        return citaRepository.findByPaciente(paciente.get());
    }

    public Optional<Paciente> getPaciente(String dni) {
        return pacienteRepository.findByDni(dni);
    }

    public Optional<Paciente> getPaciente(Long id) {
        return pacienteRepository.findById(id);
    }
}
