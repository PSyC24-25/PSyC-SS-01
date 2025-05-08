package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistroServiceTest {

    @Mock
    private MedicoService medicoService;

    @Mock
    private PacienteService pacienteService;

    @InjectMocks
    private RegistroService registroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Registrar un médico")
    void registrarMedicoExitoso() {
        when(medicoService.registrarMedico(any(Medico.class))).thenReturn(true);

        boolean resultado = registroService.registrarUsuario(
                "Carlos", "Ruiz", "pass123", "46142086S", "medico", "cardiologia");

        assertTrue(resultado);
        verify(medicoService, times(1)).registrarMedico(any(Medico.class));
        verify(pacienteService, never()).registrarPaciente(any(Paciente.class));
    }

    @Test
    @DisplayName("Registrar un paciente")
    void registrarPacienteExitoso() {
        when(pacienteService.registrarPaciente(any(Paciente.class))).thenReturn(true);

        boolean resultado = registroService.registrarUsuario(
                "Ana", "Lopez", "pass123", "82131828R", "paciente", "");

        assertTrue(resultado);
        verify(pacienteService, times(1)).registrarPaciente(any(Paciente.class));
        verify(medicoService, never()).registrarMedico(any(Medico.class));
    }
}



