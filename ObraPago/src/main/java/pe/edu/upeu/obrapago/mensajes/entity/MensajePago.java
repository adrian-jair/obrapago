package pe.edu.upeu.obrapago.mensajes.entity;

import jakarta.persistence.*;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import pe.edu.upeu.obrapago.pagos.entity.PlanillaPago;

import java.time.LocalDateTime;

@Entity
@Table(name = "MENSAJES_PAGO")
public class MensajePago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PLANILLA_ID", nullable = false)
    private PlanillaPago planilla;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OBRERO_ID", nullable = false)
    private Obrero obrero;

    @Column(length = 20)
    private String celular;

    @Column(nullable = false, length = 1000)
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMensaje estado;

    @Column(name = "ENVIADO_EN", nullable = false)
    private LocalDateTime enviadoEn = LocalDateTime.now();

    @Column(name = "DETALLE_ERROR", length = 500)
    private String detalleError;

    public MensajePago() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PlanillaPago getPlanilla() { return planilla; }
    public void setPlanilla(PlanillaPago planilla) { this.planilla = planilla; }
    public Obrero getObrero() { return obrero; }
    public void setObrero(Obrero obrero) { this.obrero = obrero; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public EstadoMensaje getEstado() { return estado; }
    public void setEstado(EstadoMensaje estado) { this.estado = estado; }
    public LocalDateTime getEnviadoEn() { return enviadoEn; }
    public void setEnviadoEn(LocalDateTime enviadoEn) { this.enviadoEn = enviadoEn; }
    public String getDetalleError() { return detalleError; }
    public void setDetalleError(String detalleError) { this.detalleError = detalleError; }
}
