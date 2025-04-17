package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.*;
import es.deusto.spq.doctorclick.repository.CitaRepository;
import es.deusto.spq.doctorclick.repository.MedicoRepository;
import es.deusto.spq.doctorclick.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
//TODO: ESTÁ UN POCO MAL XD (optimizar para que instanciar sea sencillo)
public class CitaServiceTest {

    private CitaRepository citaRepository;
    private PacienteRepository pacienteRepository;
    private MedicoRepository medicoRepository;

    private MedicoService medicoService;
    private PacienteService pacienteService;

    //@InjectMocks
    private CitaService citaService;

    //TODO: Añadir registros de médicos y paciente en los repositorios
    @BeforeEach
    void setUp() {
        citaRepository = mock(CitaRepository.class);
        pacienteRepository = mock(PacienteRepository.class);
        medicoService = mock(MedicoService.class);
        pacienteService = mock(PacienteService.class);
        citaService = new CitaService(citaRepository,pacienteRepository, medicoService, pacienteService);
    }

    @Test
    @DisplayName("Añadir una cita")
    void testAnadirCita() {
        Paciente paciente = new Paciente("73275435B","Juan","Rodriguez Sebastian","1234");
        Medico medico = new Medico("72839150J","Miguel","Sanchez","1234", Especialidad.CARDIOLOGIA);
        LocalDateTime now = LocalDateTime.now();
        Cita cita = new Cita(paciente, medico, now, Especialidad.CARDIOLOGIA,"Este es el resumen");
        //citaService.crearCita(cita);
    }


}
