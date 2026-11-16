package pe.edu.upeu.obrapago.mensajes.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.obrapago.mensajes.entity.MensajePago;

import java.util.List;

public interface MensajePagoRepository extends JpaRepository<MensajePago, Long> {
    @EntityGraph(attributePaths = {"planilla", "obrero"})
    List<MensajePago> findAllByOrderByEnviadoEnDesc();

    @EntityGraph(attributePaths = {"planilla", "obrero"})
    List<MensajePago> findByPlanillaIdOrderByEnviadoEnDesc(Long planillaId);
}
