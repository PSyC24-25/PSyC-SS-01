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
import java.util.List;
import java.util.Map;

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

        // Por ahora asignar la cita al medico que se desea sin verificaciones.
        // En un futuro esto tendria que tener en cuenta las franjas libres del medico.
        Long idMedico = Long.valueOf(requestData.get("idMedico"));
        Medico medicoEncargado = medicoService.getMedico(idMedico).get();
        cita.setEspecialidad(medicoEncargado.getEspecialidad());
        cita.setMedico(medicoEncargado);

        cita.setResumen(requestData.get("resumen"));
        citaRepository.save(cita);
        return true;
    }

    public List<Cita> obtenerCitasPorDni(String dni) {
        Paciente paciente = pacienteRepository.findByDni(dni);
        return citaRepository.findByPaciente(paciente);
    }
}
