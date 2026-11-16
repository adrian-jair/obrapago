package pe.edu.upeu.obrapago.accesos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.obrapago.accesos.entity.RegistroAcceso;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Long> {
    List<RegistroAcceso> findAllByOrderByFechaHoraDesc();
    List<RegistroAcceso> findByUsernameOrderByFechaHoraDesc(String username);
    List<RegistroAcceso> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime desde, LocalDateTime hasta);
}
