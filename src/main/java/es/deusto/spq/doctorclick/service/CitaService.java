package es.deusto.spq.doctorclick.service;

import es.deusto.spq.doctorclick.model.Cita;
import es.deusto.spq.doctorclick.model.Medico;
import es.deusto.spq.doctorclick.model.Paciente;
import es.deusto.spq.doctorclick.repository.CitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class CitaService {
    public static final int CITAS_POR_HORA = 2; // 30 min cada una
    static final int CITAS_HORA_INICIO = 8;
    static final int CITAS_HORA_FIN = 17;

    private final CitaRepository citaRepository;
    private final MedicoService medicoService;
    private final PacienteService pacienteService;

    @Autowired
    public CitaService(CitaRepository citaRepository, MedicoService medicoService, PacienteService pacienteService) {
        this.citaRepository = citaRepository;
        this.medicoService = medicoService;
        this.pacienteService = pacienteService;
    }

    private List<Cita> obtenerCitasPorDia(Long idMedico, LocalDate fecha) {
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = desde.plusDays(1);

        return citaRepository.findByMedicoDniAndFechaInDay(idMedico, desde, hasta);
    }

    public List<LocalDateTime> obtenerHorasDisponibles(Long idMedico, LocalDate fecha) {
        if(fecha.getDayOfWeek().equals(DayOfWeek.SATURDAY) || fecha.getDayOfWeek().equals(DayOfWeek.SUNDAY) || fecha.isBefore(LocalDate.now())) {
            return new ArrayList<>();
        }

        LocalDateTime horaActual = LocalDateTime.now();
        List<Cita> citasPlaneadas = obtenerCitasPorDia(idMedico, fecha);

        ArrayList<LocalDateTime> horasDisponibles = new ArrayList<>();
        for(int hora = CITAS_HORA_INICIO; hora < CITAS_HORA_FIN; hora++) {
            for(int numeroCita = 0; numeroCita < CITAS_POR_HORA; numeroCita++) {
                int minutos = 60 / CITAS_POR_HORA * numeroCita;
                LocalDateTime horaDeseada = LocalDateTime.of(fecha, LocalTime.of(hora, minutos));

                if(horaDeseada.isBefore(horaActual) || horaDeseada.isEqual(horaActual)) {
                    continue;
                }


                boolean horaOcupada = citasPlaneadas.stream().anyMatch((citaPlaneada) -> horaDeseada.equals(citaPlaneada.getFecha()));
                if(horaOcupada) {
                    continue;
                }

                horasDisponibles.add(horaDeseada);
            }
        }

        return horasDisponibles;
    }

    public enum CitaCreacionResultado {
        CITA_CREADA,
        ERROR_MEDICO_PACIENTE,
        ERROR_HORA_NO_VALIDA,
    }
    public CitaCreacionResultado crearCita(Long idMedico, String dniPaciente, LocalDateTime fecha, String razon) {
        Optional<Medico> medico = medicoService.getMedico(idMedico);
        Optional<Paciente> paciente = pacienteService.getPaciente(dniPaciente);

        if(medico.isEmpty() || paciente.isEmpty())
            return CitaCreacionResultado.ERROR_MEDICO_PACIENTE;

        // Esto se podria optimizar mas
        List<LocalDateTime> horasDisponibles = obtenerHorasDisponibles(idMedico, fecha.toLocalDate());
        boolean horaValida = horasDisponibles.stream().anyMatch(fecha::equals);
        if(!horaValida)
            return CitaCreacionResultado.ERROR_HORA_NO_VALIDA;

        Cita cita = new Cita();
        cita.setResumen(razon);
        cita.setEspecialidad(medico.get().getEspecialidad());
        cita.setMedico(medico.get());
        cita.setPaciente(paciente.get());
        cita.setFecha(fecha);

        citaRepository.save(cita);
        return CitaCreacionResultado.CITA_CREADA;
    }

    public List<Cita> obtenerCitasPorDni(String dni) {
        Optional<Paciente> paciente = pacienteService.getPaciente(dni);
        if(paciente.isEmpty())
            return new ArrayList<>();

        return citaRepository.findByPaciente(paciente.get());
    }

    public enum CitaEliminadaResultado {
        CITA_ELIMINADA,
        ERROR_MEDICO,
        ERROR_CITA_ID,
        ERROR_ELIMINACION
    }
    public CitaEliminadaResultado cancelarCitaMedico(String dni, Long id){
        Optional<Medico> medico = medicoService.getMedico(dni);
        Optional<Cita> cita = citaRepository.findById(id);

        if (medico.isEmpty()) {
            return CitaEliminadaResultado.ERROR_MEDICO;
        }
        if (cita.isEmpty()) {
            return CitaEliminadaResultado.ERROR_CITA_ID;
        }
        if (cita.get().getMedico().getDni().equals(dni)) {
            citaRepository.delete(cita.get());
            return CitaEliminadaResultado.CITA_ELIMINADA;
        }
        return CitaEliminadaResultado.ERROR_ELIMINACION;
    }
    public List<Cita> getCitas(String dni){
        return citaRepository.findByMedico_Dni(dni);
    }
    public Optional<Cita> getCita(Long id){
        return citaRepository.findById(id);
    }
    public Optional<Cita> obtenerCitaPorIdYPaciente(Long id, String dni) {
        return citaRepository.findByIdAndPacienteDni(id, dni);
    }
    public List<Cita> obtenerCitaPacientePasado(String dni) {
        return citaRepository.findByPacienteDniAndFechaBefore(dni);
    }

    public boolean cancelarCita(Long idCita, String dniPaciente) {
        Optional<Cita> optCita = obtenerCitaPorIdYPaciente(idCita, dniPaciente);
        if (optCita.isPresent()) {
            citaRepository.deleteById(idCita);
            return true;
        }
        return false;
    }

    public List<Cita> obtenerCitaMedicoPasado(String dni){
        return citaRepository.findbyMedicoDniAndFechaBefore(dni);
    }
}


