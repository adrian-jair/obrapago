package pe.edu.upeu.obrapago.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pe.edu.upeu.obrapago.accesos.entity.RegistroAcceso;
import pe.edu.upeu.obrapago.accesos.repository.RegistroAccesoRepository;
import pe.edu.upeu.obrapago.contratos.entity.Contrato;
import pe.edu.upeu.obrapago.contratos.entity.EstadoContrato;
import pe.edu.upeu.obrapago.contratos.entity.TipoContrato;
import pe.edu.upeu.obrapago.contratos.repository.ContratoRepository;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import pe.edu.upeu.obrapago.obreros.entity.Oficio;
import pe.edu.upeu.obrapago.obreros.repository.ObreroRepository;
import pe.edu.upeu.obrapago.usuarios.entity.RolUsuario;
import pe.edu.upeu.obrapago.usuarios.entity.Usuario;
import pe.edu.upeu.obrapago.usuarios.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final ObreroRepository obreroRepository;
    private final ContratoRepository contratoRepository;
    private final RegistroAccesoRepository accesoRepository;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            ObreroRepository obreroRepository,
            ContratoRepository contratoRepository,
            RegistroAccesoRepository accesoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.obreroRepository = obreroRepository;
        this.contratoRepository = contratoRepository;
        this.accesoRepository = accesoRepository;
    }

    @Override
    public void run(String... args) {
        inicializarUsuarios();
        inicializarObrerosYContratos();
        inicializarLogInicial();
    }

    private void inicializarUsuarios() {
        if (usuarioRepository.count() == 0) {
            log.info("Inicializando usuarios del sistema...");

            Usuario admin = new Usuario(null, "admin", "Administrador General", "admin@obrapago.pe", RolUsuario.ADMIN, true);
            Usuario supervisor = new Usuario(null, "supervisor", "Ing. Supervisor de Obra", "supervisor@obrapago.pe", RolUsuario.SUPERVISOR, true);
            Usuario planillero = new Usuario(null, "planillero", "Lic. Planillas y Finanzas", "planillas@obrapago.pe", RolUsuario.PLANILLERO, true);
            Usuario consulta = new Usuario(null, "obrero", "Consulta Obreros", "obrero@obrapago.pe", RolUsuario.CONSULTA, true);

            usuarioRepository.save(admin);
            usuarioRepository.save(supervisor);
            usuarioRepository.save(planillero);
            usuarioRepository.save(consulta);
            log.info("4 usuarios inicializados correctamente (admin, supervisor, planillero, obrero).");
        }
    }

    private void inicializarObrerosYContratos() {
        if (obreroRepository.count() == 0) {
            log.info("Inicializando obreros y contratos de prueba...");

            Obrero o1 = new Obrero(null, "74859632", "Juan Enrique", "Estrada Murga", Oficio.OPERARIO,
                    new BigDecimal("95.00"), "987654321", true, null);
            Obrero o2 = new Obrero(null, "45896321", "Carlos Alberto", "Ramos Quispe", Oficio.MAESTRO,
                    new BigDecimal("130.00"), "976543210", true, null);
            Obrero o3 = new Obrero(null, "71234568", "Pedro Luis", "Mamani Condori", Oficio.PEON,
                    new BigDecimal("75.00"), "965432109", true, null);

            o1 = obreroRepository.save(o1);
            o2 = obreroRepository.save(o2);
            o3 = obreroRepository.save(o3);

            Contrato c1 = new Contrato(null, o1, "CTR-2026-001", TipoContrato.POR_OBRA,
                    LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(6),
                    new BigDecimal("95.00"), "Operario de Albañilería", EstadoContrato.ACTIVO, "Contrato Obra Edificio Central");

            Contrato c2 = new Contrato(null, o2, "CTR-2026-002", TipoContrato.PLAZO_FIJO,
                    LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(9),
                    new BigDecimal("130.00"), "Maestro Mayor de Obra", EstadoContrato.ACTIVO, "Supervisión estructural");

            Contrato c3 = new Contrato(null, o3, "CTR-2026-003", TipoContrato.POR_JORNAL,
                    LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(3),
                    new BigDecimal("75.00"), "Peón de Descarga y Acarreo", EstadoContrato.ACTIVO, "Turno diurno");

            contratoRepository.save(c1);
            contratoRepository.save(c2);
            contratoRepository.save(c3);

            log.info("Obreros y contratos inicializados con éxito.");
        }
    }

    private void inicializarLogInicial() {
        if (accesoRepository.count() == 0) {
            RegistroAcceso logSistema = new RegistroAcceso(
                    null,
                    "SYSTEM",
                    "SISTEMA",
                    "127.0.0.1",
                    "Spring Boot Server",
                    "INICIO_SISTEMA",
                    LocalDateTime.now(),
                    true,
                    "Arranque exitoso del servidor ObraPago con soporte de 10 módulos y seguridad"
            );
            accesoRepository.save(logSistema);
        }
    }
}
