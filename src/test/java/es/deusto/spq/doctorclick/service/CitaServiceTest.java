package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.*;
import es.deusto.spq.doctorclick.repository.CitaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;
    @Mock
    private MedicoService medicoService;
    @Mock
    private PacienteService pacienteService;

    @InjectMocks
    private CitaService citaService;

    private Paciente paciente;
    private Medico medico;
    private Cita cita;
    private LocalDateTime fecha1;

    @BeforeEach
    void setUp() {

        paciente = new Paciente("73275435B", "Juan", "Rodriguez Sebastian", "1234");
        medico = new Medico("72839150J", "Miguel", "Sanchez", "1234", Especialidad.CARDIOLOGIA);
        fecha1 = LocalDateTime.of(2025,10,10,10,0);
        cita = new Cita(paciente, medico, fecha1, Especialidad.CARDIOLOGIA, "Este es el resumen");
        lenient().when(medicoService.getMedico(medico.getId())).thenReturn(Optional.of(medico));
        lenient().when(pacienteService.getPaciente(paciente.getDni())).thenReturn(Optional.of(paciente));

        lenient().when(citaRepository.findByMedicoDniAndFechaInDay(medico.getId(), fecha1.toLocalDate().atStartOfDay(), fecha1.toLocalDate().plusDays(1).atStartOfDay()))
                .thenReturn(Collections.emptyList());

    }

    @Test
    @DisplayName("Añadir una cita")
    void testAnadirCita() {

        CitaService.CitaCreacionResultado resultado = citaService.crearCita(medico.getId(), paciente.getDni(), fecha1, cita.getResumen());

        verify(medicoService, times(1)).getMedico(medico.getId());
        verify(pacienteService, times(1)).getPaciente(paciente.getDni());
        verify(citaRepository, times(1)).save(any(Cita.class));

        assertEquals(CitaService.CitaCreacionResultado.CITA_CREADA, resultado);
    }

    @Test
    @DisplayName("Cita por DNI")
    void testCitaPorDni() {
        when(citaRepository.findByPacienteDniAndFechaAfter(paciente.getDni(), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS))).thenReturn(List.of(cita));

        List<Cita> resultado = citaService.obtenerCitaPacienteFuturo(paciente.getDni());

        assertEquals(List.of(cita), resultado);
    }

    @Test
    @DisplayName("Cita por DNI que no tiene cita")
    void testCitaPorDniSinCita() {
        when(citaRepository.findByPacienteDniAndFechaAfter(paciente.getDni(), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS))).thenReturn(List.of());

        List<Cita> resultado = citaService.obtenerCitaPacienteFuturo(paciente.getDni());
        assertEquals(List.of(), resultado);
    }

    @Test
    @DisplayName("Eliminar cita médico")
    void testEliminarCita() {

        when(citaRepository.findById(cita.getId())).thenReturn(Optional.of(cita));
        when(medicoService.getMedico(medico.getDni())).thenReturn(Optional.of(medico));

        CitaService.CitaEliminadaResultado resultado = citaService.cancelarCitaMedico(medico.getDni(), cita.getId());
        verify(citaRepository, times(1)).delete(cita);

        assertEquals(CitaService.CitaEliminadaResultado.CITA_ELIMINADA, resultado);
    }

    @Test
    @DisplayName("Eliminar cita paciente")
    void testEliminarCitaPaciente() {
        when(citaRepository.findByIdAndPacienteDni(cita.getId(), paciente.getDni())).thenReturn(Optional.of(cita));
        boolean resultado = citaService.cancelarCita(cita.getId(), paciente.getDni());
        verify(citaRepository, times(1)).deleteById(cita.getId());
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Test de cita sencillo")
    void testCitaSencillo() {
        assertEquals(paciente, cita.getPaciente());
        assertEquals(Especialidad.CARDIOLOGIA, cita.getEspecialidad());
        assertEquals(fecha1, cita.getFecha());
    }

    @Test
    @DisplayName("Eliminar todas las citas de un médico")
    void testEliminarCitasMedico() {
        when(citaRepository.findByMedicoDni(medico.getDni())).thenReturn(List.of(cita));
        when(citaRepository.findById(cita.getId())).thenReturn(Optional.of(cita));
        when(medicoService.getMedico(medico.getDni())).thenReturn(Optional.of(medico));

        boolean resultado = citaService.eliminarCitasMedicos(medico.getDni());

        assertTrue(resultado);
        verify(citaRepository, times(1)).delete(cita);
    }


    @Test
    @DisplayName("Obtener citas pasadas del médico")
    void testObtenerCitaMedicoPasado() {
        when(citaRepository.findByMedicoDniAndFechaBefore(medico.getDni(), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)))
                .thenReturn(List.of());

        List<Cita> resultado = citaService.obtenerCitaMedicoPasado(medico.getDni());

        assertEquals(0, resultado.size());
    }


    @Test
    @DisplayName("Obtener citas pasadas del paciente")
    void testObtenerCitaPacientePasado() {
        when(citaRepository.findByPacienteDniAndFechaBefore(paciente.getDni(), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)))
                .thenReturn(List.of());

        List<Cita> resultado = citaService.obtenerCitaPacientePasado(paciente.getDni());

        assertEquals(0, resultado.size());
    }

    @Test
    @DisplayName("Obtener citas pasadas del paciente")
    void testObtenerCitaPacienteFuturo() {
        when(citaRepository.findByPacienteDniAndFechaAfter(paciente.getDni(), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)))
                .thenReturn(List.of(cita));

        List<Cita> resultado = citaService.obtenerCitaPacientePasado(paciente.getDni());

        assertEquals(0, resultado.size());
    }

    @Test
    @DisplayName("Obtener cita por ID y DNI del paciente")
    void testObtenerCitaPorIdYPaciente() {
        when(citaRepository.findByIdAndPacienteDni(cita.getId(), paciente.getDni()))
                .thenReturn(Optional.of(cita));

        Optional<Cita> resultado = citaService.obtenerCitaPorIdYPaciente(cita.getId(), paciente.getDni());

        assertTrue(resultado.isPresent());
        assertEquals(cita, resultado.get());
    }

    @Test
    @DisplayName("Obtener cita por ID")
    void testGetCitaPorId() {
        when(citaRepository.findById(cita.getId())).thenReturn(Optional.of(cita));

        Optional<Cita> resultado = citaService.getCita(cita.getId());

        assertTrue(resultado.isPresent());
        assertEquals(cita, resultado.get());
    }


    @Test
    @DisplayName("Obtener citas por DNI de médico")
    void testGetCitasPorDniMedico() {
        when(citaRepository.findByMedicoDni(medico.getDni())).thenReturn(List.of(cita));

        List<Cita> citas = citaService.getCitas(medico.getDni());

        assertEquals(1, citas.size());
        assertEquals(cita, citas.get(0));
    }
}