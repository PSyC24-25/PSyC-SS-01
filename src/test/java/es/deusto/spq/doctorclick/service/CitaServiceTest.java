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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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

        CitaService.CitaEliminadaResultado resultado = citaService.cancelarCitaDeMedico(medico.getDni(), cita.getId());
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

    @Test
    @DisplayName("Cita en fin de semana")
    void finDeSemana() {
        LocalDate sabado = LocalDate.of(2025, 5, 24);  // es sábado
        List<LocalDateTime> slots = citaService.obtenerHorasDisponibles(42L, sabado);
        assertTrue(slots.isEmpty(), "Debe estar vacío en fin de semana");
    }

    @Test
    @DisplayName("Verificar que al no haber citas, el tamaño de la lista coincide y el primer hueco es el que empieza la jornada")
    void diaLaborSinCitas() {
        LocalDate maniana = LocalDate.now().plusDays(1);
        // simular que no hay citas ese día
        when(citaRepository.findByMedicoDniAndFechaInDay(
                eq(42L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<LocalDateTime> slots = citaService.obtenerHorasDisponibles(42L, maniana);

        int totalEsperado = (CitaService.CITAS_HORA_FIN - CitaService.CITAS_HORA_INICIO)
                * CitaService.CITAS_POR_HORA;
        assertEquals(totalEsperado, slots.size(),
                "Debe devolver todos los huecos: " + totalEsperado);

        // primer slot a las 8:00
        assertEquals(LocalDateTime.of(maniana, LocalTime.of(
                        CitaService.CITAS_HORA_INICIO, 0)),
                slots.get(0));
    }

    @Test
    @DisplayName("Simular cita a las 10:00 y verificar que no aparece como opción")
    void unSlotBloqueado() {
        LocalDate maniana = LocalDate.now().plusDays(1);
        LocalDateTime bloqueado = LocalDateTime.of(maniana, LocalTime.of(10, 0));
        Cita cita = new Cita();
        cita.setFecha(bloqueado);

        when(citaRepository.findByMedicoDniAndFechaInDay(
                eq(42L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(cita));

        List<LocalDateTime> slots = citaService.obtenerHorasDisponibles(42L, maniana);

        assertFalse(slots.contains(bloqueado),
                "El hueco 10:00 debe estar excluido");
    }


}