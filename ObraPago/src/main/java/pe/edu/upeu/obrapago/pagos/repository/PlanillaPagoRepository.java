package pe.edu.upeu.obrapago.pagos.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.obrapago.pagos.entity.PlanillaPago;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanillaPagoRepository extends JpaRepository<PlanillaPago, Long> {
    boolean existsByFechaInicioAndFechaFin(LocalDate fechaInicio, LocalDate fechaFin);

    @EntityGraph(attributePaths = {"detalles", "detalles.obrero"})
    Optional<PlanillaPago> findOneById(Long id);

    @EntityGraph(attributePaths = {"detalles", "detalles.obrero"})
    List<PlanillaPago> findAllByOrderByFechaInicioDesc();

    List<PlanillaPago> findAllByOrderByFechaInicioAscIdAsc();
}
