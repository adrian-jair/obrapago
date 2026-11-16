package pe.edu.upeu.obrapago;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pe.edu.upeu.obrapago.asistencia.dto.AsistenciaRequest;
import pe.edu.upeu.obrapago.asistencia.dto.DetalleAsistenciaRequest;
import pe.edu.upeu.obrapago.asistencia.entity.EstadoAsistencia;
import pe.edu.upeu.obrapago.asistencia.repository.JornadaAsistenciaRepository;
import pe.edu.upeu.obrapago.asistencia.service.AsistenciaService;
import pe.edu.upeu.obrapago.common.exception.BusinessException;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import pe.edu.upeu.obrapago.obreros.entity.Oficio;
import pe.edu.upeu.obrapago.obreros.repository.ObreroRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.test.context.ActiveProfiles;
import pe.edu.upeu.obrapago.contratos.repository.ContratoRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AsistenciaTransactionTest {

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private ObreroRepository obreroRepository;

    @Autowired
    private JornadaAsistenciaRepository jornadaRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @BeforeEach
    void limpiar() {
        jornadaRepository.deleteAll();
        contratoRepository.deleteAll();
        obreroRepository.deleteAll();
    }

    @Test
    void debeHacerRollbackSiUnDetalleEsInvalido() {
        Obrero valido = new Obrero();
        valido.setDni("74859632");
        valido.setNombres("Juan");
        valido.setApellidos("Perez");
        valido.setOficio(Oficio.OPERARIO);
        valido.setTarifaJornal(new BigDecimal("90.00"));
        valido.setActivo(true);
        valido = obreroRepository.save(valido);

        AsistenciaRequest request = new AsistenciaRequest(
                LocalDate.of(2026, 9, 14),
                List.of(
                        new DetalleAsistenciaRequest(
                                valido.getId(), EstadoAsistencia.PRESENTE,
                                new BigDecimal("2.00"), "Jornada normal"),
                        new DetalleAsistenciaRequest(
                                999999L, EstadoAsistencia.PRESENTE,
                                BigDecimal.ZERO, "Obrero inexistente")
                )
        );

        assertThrows(BusinessException.class, () -> asistenciaService.registrar(request));
        assertEquals(0, jornadaRepository.count(),
                "La cabecera debe revertirse si falla cualquier detalle");
    }
}
