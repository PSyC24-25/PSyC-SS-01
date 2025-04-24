package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.repository.PacienteRepository;
import es.deusto.spq.doctorclick.service.PacienteService;

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
    @DisplayName("Registrar paciente nuevo correctamenete")
    void registrarPaciente() {
        Paciente paciente = new Paciente("97147611G","Sara","García","pass123");

        //Simular que no existe el paciente
        when(pacienteRepository.findByDni("12345678A")).thenReturn(Optional.empty());

        boolean resultado = pacienteService.registrarPaciente(paciente);
        assertTrue(resultado);
        verify(pacienteRepository).save(paciente);

    }


}
