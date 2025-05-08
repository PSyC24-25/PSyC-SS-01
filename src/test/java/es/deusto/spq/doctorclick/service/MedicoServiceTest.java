package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Especialidad;
import es.deusto.spq.doctorclick.repository.MedicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicoServiceTest {

    @Mock
    MedicoRepository medicoRepository;

    @InjectMocks
    MedicoService medicoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Registrar médico nuevo")
    void registrarMedicoNuevo() {
        Medico medico = new Medico("86219884Z", "Luis", "Martínez", "pass123", Especialidad.PEDIATRIA);

        when(medicoRepository.findByDni("86219884Z")).thenReturn(Optional.empty());

        boolean resultado = medicoService.registrarMedico(medico);

        assertTrue(resultado);
        verify(medicoRepository).save(medico);
    }

    @Test
    @DisplayName("No registrar médico ya existente")
    void noRegistrarMedicoExistente() {
        Medico medico = new Medico("21364511H", "Laura", "Sánchez", "pass123", Especialidad.PSIQUIATRIA);

        when(medicoRepository.findByDni("21364511H")).thenReturn(Optional.of(medico));

        boolean resultado = medicoService.registrarMedico(medico);

        assertFalse(resultado);
        verify(medicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener todos los médicos")
    void obtenerTodosLosMedicos() {
        Medico m1 = new Medico("97998104A", "Mario", "Ruiz", "pass123", Especialidad.CARDIOLOGIA);
        Medico m2 = new Medico("89002143D", "Clara", "Moreno", "pass123", Especialidad.ONCOLOGIA);

        when(medicoRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        List<Medico> resultado = medicoService.getMedicos();

        assertEquals(2, resultado.size());
        assertEquals("Mario", resultado.get(0).getNombre());
        assertEquals("Clara", resultado.get(1).getNombre());
    }

    @Test
    @DisplayName("Obtener médico por DNI")
    void obtenerMedicoPorDni() {
        Medico medico = new Medico("19689791C", "Andrés", "Gómez", "pass123", Especialidad.NEUROLOGIA);

        when(medicoRepository.findByDni("19689791C")).thenReturn(Optional.of(medico));

        Optional<Medico> resultado = medicoService.getMedico("19689791C");

        assertTrue(resultado.isPresent());
        assertEquals("19689791C", resultado.get().getDni());
    }

    @Test
    @DisplayName("Obtener médico por ID")
    void obtenerMedicoPorId() {
        Medico medico = new Medico("55817720D", "Juan", "Maroto", "pass123", Especialidad.NEUROLOGIA);
        Long id = medico.getId();

        when(medicoRepository.findById(id)).thenReturn(Optional.of(medico));

        Optional<Medico> resultado = medicoService.getMedico(id);

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Test sencillo medico")
    void crearMedico() {
        Medico medico = new Medico();
        medico.setEspecialidad(Especialidad.NEUROLOGIA);
        assertEquals(Especialidad.NEUROLOGIA, medico.getEspecialidad());
    }

}
