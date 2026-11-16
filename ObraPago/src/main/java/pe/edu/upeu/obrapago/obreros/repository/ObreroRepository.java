package pe.edu.upeu.obrapago.obreros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;

import java.util.List;
import java.util.Optional;

public interface ObreroRepository extends JpaRepository<Obrero, Long> {
    boolean existsByDni(String dni);
    boolean existsByDniAndIdNot(String dni, Long id);
    Optional<Obrero> findByIdAndActivoTrue(Long id);
    Optional<Obrero> findByDni(String dni);
    Optional<Obrero> findByDniAndActivoTrue(String dni);
    List<Obrero> findAllByActivoTrueOrderByApellidosAscNombresAsc();
}
