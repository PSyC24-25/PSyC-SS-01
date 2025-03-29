package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Especialidad;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.model.Usuario;
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

    public void registrarUsuario(String nombre, String apellidos, String contrasenia, String dni, String tipo, String especialidad) {
        Usuario usuario = null;

        if(tipo.equals("medico")) {
            usuario = new Medico(Especialidad.valueOf(especialidad.toUpperCase()));
        } else if(tipo.equals("paciente")) {
            usuario = new Paciente();
        }

        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setDni(dni);
        usuario.setContrasenia(contrasenia);

        if(tipo.equals("medico")) {
            medicoService.registrarMedico((Medico)usuario);
        } else {
            pacienteService.registrarPaciente((Paciente)usuario);
        }
    }
}
