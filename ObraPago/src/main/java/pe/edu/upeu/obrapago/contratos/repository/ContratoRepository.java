package pe.edu.upeu.obrapago.contratos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.obrapago.contratos.entity.Contrato;
import pe.edu.upeu.obrapago.contratos.entity.EstadoContrato;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    List<Contrato> findByObreroIdOrderByFechaInicioDesc(Long obreroId);
    List<Contrato> findByEstadoOrderByFechaInicioDesc(EstadoContrato estado);
    Optional<Contrato> findByCodigoContrato(String codigoContrato);
    boolean existsByCodigoContrato(String codigoContrato);
    Optional<Contrato> findFirstByObreroIdAndEstadoOrderByFechaInicioDesc(Long obreroId, EstadoContrato estado);
}
