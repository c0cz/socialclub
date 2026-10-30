package ar.edu.siglo21.socialclub.service;

import ar.edu.siglo21.socialclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socialclub.exception.ReglaNegocioException;
import ar.edu.siglo21.socialclub.model.Socio;
import ar.edu.siglo21.socialclub.repository.SocioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SocioService {

    private final SocioRepository socioRepository;

    public SocioService(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    public Page<Socio> listar(String apellido, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        if (apellido == null || apellido.isBlank()) {
            return socioRepository.findAll(pageable);
        }
        return socioRepository.findByApellidoContainingIgnoreCase(apellido.trim(), pageable);
    }

    public Socio buscarPorId(Long id) {
        return socioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado"));
    }

    public Socio buscarPorDni(String dni) {
        return socioRepository.findByDni(dni)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un socio con ese DNI"));
    }

    public Socio guardar(Socio socio) {
        // RN7 también en el servidor: nunca confiar solo en el navegador
        if (socio.getDni() == null || !socio.getDni().matches("\\d{7,8}")) {
            throw new ReglaNegocioException("RN7",
                    "El DNI debe tener 7 u 8 números, sin puntos");
        }

        // DNI único (si es otro socio el que ya lo tiene)
        socioRepository.findByDni(socio.getDni()).ifPresent(existente -> {
            if (!existente.getId().equals(socio.getId())) {
                throw new ReglaNegocioException("DNI_DUPLICADO",
                        "Ya existe un socio con ese DNI");
            }
        });

        return socioRepository.save(socio);
    }
}