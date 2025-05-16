package es.deusto.spq.doctorclick.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.deusto.spq.doctorclick.model.Especialidad;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.repository.MedicoRepository;
import es.deusto.spq.doctorclick.repository.PacienteRepository;
import es.deusto.spq.doctorclick.service.CitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private CitaService citaService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Paciente paciente;
    private Medico medico;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create test data
        paciente = new Paciente("73275435B", "Juan", "Rodriguez Sebastian", "1234");
        medico = new Medico("72839150J", "Miguel", "Sanchez", "1234", Especialidad.CARDIOLOGIA);

        // Set ID using reflection since there's no setter
        Field idField = medico.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(medico, 1L);

        // Mock repository responses
        when(pacienteRepository.findByDni("73275435B")).thenReturn(Optional.of(paciente));
        when(medicoRepository.findByDni("72839150J")).thenReturn(Optional.of(medico));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));

        // Mock CitaService
        when(citaService.crearCita(any(), anyString(), any(), anyString()))
                .thenReturn(CitaService.CitaCreacionResultado.CITA_CREADA);
    }

    @Test
    public void testLoginAndCreateAppointment() throws Exception {
        // 1. Login as a patient
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "73275435B");
        loginRequest.put("password", "1234");
        loginRequest.put("tipoUsuario", "paciente");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("JWT"))
                .andReturn();

        // Get the JWT cookie
        String jwtCookie = loginResult.getResponse().getCookie("JWT").getValue();
        System.out.println("[DEBUG_LOG] JWT Cookie: " + jwtCookie);

        // 2. Create an appointment
        Map<String, String> appointmentRequest = new HashMap<>();
        appointmentRequest.put("idMedico", "1");
        appointmentRequest.put("razon", "Consulta de prueba");

        // Set date for appointment (future date)
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        // Make sure it's during working hours (8-17) and not on weekend
        if (appointmentDate.getHour() < 8 || appointmentDate.getHour() >= 17) {
            appointmentDate = appointmentDate.withHour(10).withMinute(0);
        }

        appointmentRequest.put("anyo", String.valueOf(appointmentDate.getYear()));
        appointmentRequest.put("mes", String.valueOf(appointmentDate.getMonthValue()));
        appointmentRequest.put("dia", String.valueOf(appointmentDate.getDayOfMonth()));
        appointmentRequest.put("hora", String.valueOf(appointmentDate.getHour()));
        appointmentRequest.put("minutos", String.valueOf(appointmentDate.getMinute()));

        MvcResult appointmentResult = mockMvc.perform(post("/api/paciente/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequest))
                .cookie(loginResult.getResponse().getCookies()))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("[DEBUG_LOG] Appointment response: " + appointmentResult.getResponse().getContentAsString());
    }
}
