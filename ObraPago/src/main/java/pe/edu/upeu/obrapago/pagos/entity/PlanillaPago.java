package pe.edu.upeu.obrapago.pagos.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PLANILLAS_PAGO",
       uniqueConstraints = @UniqueConstraint(name = "UK_PLANILLA_PERIODO", columnNames = {"FECHA_INICIO", "FECHA_FIN"}))
public class PlanillaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "GENERADA_EN", nullable = false)
    private LocalDateTime generadaEn = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPlanilla estado = EstadoPlanilla.GENERADA;

    @Column(name = "TOTAL_NETO", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalNeto = BigDecimal.ZERO;

    @OneToMany(mappedBy = "planilla", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePago> detalles = new ArrayList<>();

    public PlanillaPago() {}

    public void agregarDetalle(DetallePago detalle) {
        detalles.add(detalle);
        detalle.setPlanilla(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public LocalDateTime getGeneradaEn() { return generadaEn; }
    public void setGeneradaEn(LocalDateTime generadaEn) { this.generadaEn = generadaEn; }
    public EstadoPlanilla getEstado() { return estado; }
    public void setEstado(EstadoPlanilla estado) { this.estado = estado; }
    public BigDecimal getTotalNeto() { return totalNeto; }
    public void setTotalNeto(BigDecimal totalNeto) { this.totalNeto = totalNeto; }
    public List<DetallePago> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePago> detalles) { this.detalles = detalles; }
}
