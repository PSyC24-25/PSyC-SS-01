package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Especialidad;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class RegistroService {

    private final MedicoService medicoService;
    private final PacienteService pacienteService;

    @Autowired
    public RegistroService(MedicoService medicoService, PacienteService pacienteService) {
        this.medicoService = medicoService;
        this.pacienteService = pacienteService;
    }

    public void registrar(String nombre, String apellido, String contrasenia, String dni, String tipo, String especialidad) {
        if (tipo.equals("medico")) {
            Medico medico = new Medico();
            medico.setNombre(nombre);
            medico.setApellido(apellido);
            medico.setDni(dni);
            medico.setContrasenia(contrasenia);
            medico.setEspecialidad(Especialidad.valueOf(especialidad.toUpperCase()));
            medicoService.registrarMedico(medico);
        }
        else if (tipo.equals("paciente")) {
            Paciente paciente = new Paciente();
            paciente.setNombre(nombre);
            paciente.setApellido(apellido);
            paciente.setDni(dni);
            paciente.setContrasenia(contrasenia);
            paciente.setDni(dni);
            pacienteService.registrarPaciente(paciente);
        }
    }
}
