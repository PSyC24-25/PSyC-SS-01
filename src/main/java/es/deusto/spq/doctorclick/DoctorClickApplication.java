package es.deusto.spq.doctorclick;

import es.deusto.spq.doctorclick.repository.PacienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DoctorClickApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoctorClickApplication.class, args);
    }

    @Bean
    public CommandLineRunner crearPaciente(PacienteRepository pacienteRepository) {
        return args -> {
            // Paciente paciente = new Paciente();
            // paciente.setNombre("Nombre");
            // paciente.setApellidos("Apellidos");
            // paciente.setContrasenia("1234");
            // paciente.setDni("1234");
            // pacienteRepository.save(paciente);
        };
    }
}
