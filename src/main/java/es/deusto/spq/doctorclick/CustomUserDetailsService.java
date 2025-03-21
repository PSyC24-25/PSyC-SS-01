package es.deusto.spq.doctorclick;

import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.MedicoDetails;
import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.model.PacienteDetails;
import es.deusto.spq.doctorclick.repository.MedicoRepository;
import es.deusto.spq.doctorclick.repository.PacienteRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public CustomUserDetailsService(MedicoRepository medicoRepository, PacienteRepository pacienteRepository) {
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Medico medico = medicoRepository.findByDni(username);
        if(medico != null) {
            return new MedicoDetails(medico);
        }

        Paciente paciente = pacienteRepository.findByDni(username);
        if(paciente != null) {
            return new PacienteDetails(paciente);
        }

        throw new UsernameNotFoundException("Usuario no encontrado");
    }
}
