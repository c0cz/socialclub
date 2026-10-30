// Servicio para gestionar las inscripciones de socios a actividades
package ar.edu.siglo21.socialclub.service;

import ar.edu.siglo21.socialclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socialclub.exception.ReglaNegocioException;
import ar.edu.siglo21.socialclub.model.*;
import ar.edu.siglo21.socialclub.repository.ActividadRepository;
import ar.edu.siglo21.socialclub.repository.InscripcionRepository;
import ar.edu.siglo21.socialclub.repository.SocioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class InscripcionService {

    private static final int MAX_ACTIVIDADES = 3;

    private final InscripcionRepository inscripcionRepository;
    private final SocioRepository socioRepository;
    private final ActividadRepository actividadRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository,
                              SocioRepository socioRepository,
                              ActividadRepository actividadRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.socioRepository = socioRepository;
        this.actividadRepository = actividadRepository;
    }

    @Transactional
    public Inscripcion inscribir(Long socioId, Long actividadId) {
        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado"));
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada"));

        // RN4: cuota al día
        if (socio.getEstadoCuota() == EstadoCuota.MOROSO) {
            throw new ReglaNegocioException("RN4", "Debe regularizar su cuota en secretaría");
        }

        // RN2: sin duplicados
        if (inscripcionRepository.existsBySocioIdAndActividadIdAndEstado(
                socioId, actividadId, EstadoInscripcion.ACTIVA)) {
            throw new ReglaNegocioException("RN2", "El socio ya está inscripto en esta actividad");
        }

        // RN3: edad dentro del rango
        int edad = Period.between(socio.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < actividad.getEdadMinima() || edad > actividad.getEdadMaxima()) {
            throw new ReglaNegocioException("RN3",
                    "La edad del socio (" + edad + " años) está fuera del rango permitido: de "
                    + actividad.getEdadMinima() + " a " + actividad.getEdadMaxima() + " años");
        }

        // RN5: máximo 3 actividades
        long activas = inscripcionRepository.countBySocioIdAndEstado(socioId, EstadoInscripcion.ACTIVA);
        if (activas >= MAX_ACTIVIDADES) {
            throw new ReglaNegocioException("RN5", "Alcanzó el máximo de 3 actividades");
        }

        // RN1: cupo
        long ocupados = inscripcionRepository.contarActivasPorActividad(actividadId);
        if (ocupados >= actividad.getCupoMaximo()) {
            throw new ReglaNegocioException("RN1", "La actividad no tiene cupo disponible");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setSocio(socio);
        inscripcion.setActividad(actividad);
        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setEstado(EstadoInscripcion.ACTIVA);
        return inscripcionRepository.save(inscripcion);
    }

    @Transactional
    public Inscripcion darDeBaja(Long inscripcionId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada"));
        inscripcion.setEstado(EstadoInscripcion.BAJA);
        return inscripcionRepository.save(inscripcion);
    }

    public long cupoDisponible(Actividad actividad) {
        return actividad.getCupoMaximo()
                - inscripcionRepository.contarActivasPorActividad(actividad.getId());
    }

    public List<Inscripcion> activasDeSocio(Long socioId) {
        return inscripcionRepository.findBySocioIdAndEstado(socioId, EstadoInscripcion.ACTIVA);
    }

    public List<Inscripcion> activasDeActividad(Long actividadId) {
        return inscripcionRepository.findByActividadIdAndEstado(actividadId, EstadoInscripcion.ACTIVA);
    }
}
