package pe.edu.upeu.obrapago.respaldo.entity;

import jakarta.persistence.*;
import pe.edu.upeu.obrapago.pagos.entity.PlanillaPago;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESPALDOS")
public class Respaldo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PLANILLA_ID", nullable = false)
    private PlanillaPago planilla;

    @Column(name = "NOMBRE_ARCHIVO", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "RUTA_ARCHIVO", nullable = false, length = 1000)
    private String rutaArchivo;

    @Column(name = "GENERADO_EN", nullable = false)
    private LocalDateTime generadoEn = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRespaldo estado;

    public Respaldo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PlanillaPago getPlanilla() { return planilla; }
    public void setPlanilla(PlanillaPago planilla) { this.planilla = planilla; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public LocalDateTime getGeneradoEn() { return generadoEn; }
    public void setGeneradoEn(LocalDateTime generadoEn) { this.generadoEn = generadoEn; }
    public EstadoRespaldo getEstado() { return estado; }
    public void setEstado(EstadoRespaldo estado) { this.estado = estado; }
}
