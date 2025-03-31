package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Especialidad;
import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.repository.CitaRepository;
import es.deusto.spq.doctorclick.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository, CitaRepository citaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
    }

    public boolean registrarPaciente(Paciente paciente){
        Paciente user = pacienteRepository.findByDni(paciente.getDni());
        if(user != null){
            return false;
        }
        pacienteRepository.save(paciente);
        return true;
    }

    public boolean crearCita(Map<String, String> requestData, String dni) {
        Paciente paciente = null;
        if ((paciente = pacienteRepository.findByDni(dni)) == null) {
            return false;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(requestData.get("fecha"));
        sb.append(" ");
        sb.append(requestData.get("hora"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime fecha = LocalDateTime.parse(sb.toString(), dtf);

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setFecha(fecha);
        cita.setEspecialidad(Especialidad.valueOf(requestData.get("especialidad").toUpperCase()));
        cita.setResumen(requestData.get("resumen"));
        citaRepository.save(cita);
        return true;
    }
}
