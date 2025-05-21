package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.repository.PacienteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class PacienteServiceTest {
    @Mock
    PacienteRepository pacienteRepository;

    @InjectMocks
    PacienteService pacienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Activa las anotaciones @Mock y @InjectMocks
    }

    @Test
    @DisplayName("Registrar paciente nuevo")
    void registrarPaciente() {
        Paciente paciente = new Paciente("97147611G","Sara","García","pass123");

        //Simular que no existe el paciente
        when(pacienteRepository.findByDni("97147611G")).thenReturn(Optional.empty());

        boolean resultado = pacienteService.registrarPaciente(paciente);
        assertTrue(resultado);
        verify(pacienteRepository).save(paciente);

    }

    @Test
    @DisplayName("No registrar si el paciente existe")
    void noRegistrarPacienteExistente() {
        Paciente paciente = new Paciente("03982869M", "Ana", "López", "pass123");

        // Simula que existe paciente
        when(pacienteRepository.findByDni("03982869M")).thenReturn(Optional.of(paciente));

        boolean resultado = pacienteService.registrarPaciente(paciente);

        assertFalse(resultado);
        verify(pacienteRepository, never()).save(any());
    }


    @Test
    @DisplayName("Obtener paciente por DNI")
    void obtenerPacientePorDni() {
        Paciente paciente = new Paciente("57343262F", "Pedro", "Rodríguez", "pass123");
        when(pacienteRepository.findByDni("57343262F")).thenReturn(Optional.of(paciente));

        Optional<Paciente> resultado = pacienteService.getPaciente("57343262F");

        assertTrue(resultado.isPresent());
        assertEquals("Pedro", resultado.get().getNombre());
        assertEquals("Rodríguez", resultado.get().getApellidos());
        assertEquals("pass123", resultado.get().getContrasenia());
    }

    @Test
    @DisplayName("Obtener paciente inexistente por DNI")
    void obtenerPacienteInexistentePorDni() {
        String dni = "00000000A";

        when(pacienteRepository.findByDni(dni)).thenReturn(Optional.empty());
        Optional<Paciente> resultado = pacienteService.getPaciente(dni);
        assertTrue(resultado.isEmpty());
    }


    @Test
    @DisplayName("Obtener paciente por ID")
    void obtenerPacientePorId() {
        Paciente paciente = new Paciente("55817720D", "Lucía", "Mora", "pass123");
        Long id = paciente.getId();

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(paciente));

        Optional<Paciente> resultado = pacienteService.getPaciente(id);

        assertTrue(resultado.isPresent());
        assertEquals("Lucía", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Obtener paciente inexistente por ID")
    void obtenerPacienteInexistentePorId() {
        Long id = 1234L;

        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Paciente> resultado = pacienteService.getPaciente(id);
        assertTrue(resultado.isEmpty());
    }
}
