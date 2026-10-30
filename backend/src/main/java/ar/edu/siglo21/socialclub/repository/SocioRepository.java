package ar.edu.siglo21.socialclub.repository;

import ar.edu.siglo21.socialclub.model.Socio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SocioRepository extends JpaRepository<Socio, Long> {

    Optional<Socio> findByDni(String dni);

    boolean existsByDni(String dni);

    Page<Socio> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);
}