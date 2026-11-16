package pe.edu.upeu.obrapago.pagos.entity;

import jakarta.persistence.*;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;

import java.math.BigDecimal;

@Entity
@Table(name = "DETALLES_PAGO",
       uniqueConstraints = @UniqueConstraint(name = "UK_PLANILLA_PAGO_OBRERO", columnNames = {"PLANILLA_ID", "OBRERO_ID"}))
public class DetallePago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PLANILLA_ID", nullable = false)
    private PlanillaPago planilla;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OBRERO_ID", nullable = false)
    private Obrero obrero;

    @Column(name = "DIAS_LABORADOS", nullable = false)
    private Long diasLaborados;

    @Column(name = "HORAS_EXTRA", nullable = false, precision = 8, scale = 2)
    private BigDecimal horasExtra;

    @Column(name = "TARIFA_JORNAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaJornal;

    @Column(name = "TARIFA_HORA", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaHora;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal bruto;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal adelantos;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal neto;

    public DetallePago() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PlanillaPago getPlanilla() { return planilla; }
    public void setPlanilla(PlanillaPago planilla) { this.planilla = planilla; }
    public Obrero getObrero() { return obrero; }
    public void setObrero(Obrero obrero) { this.obrero = obrero; }
    public Long getDiasLaborados() { return diasLaborados; }
    public void setDiasLaborados(Long diasLaborados) { this.diasLaborados = diasLaborados; }
    public BigDecimal getHorasExtra() { return horasExtra; }
    public void setHorasExtra(BigDecimal horasExtra) { this.horasExtra = horasExtra; }
    public BigDecimal getTarifaJornal() { return tarifaJornal; }
    public void setTarifaJornal(BigDecimal tarifaJornal) { this.tarifaJornal = tarifaJornal; }
    public BigDecimal getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(BigDecimal tarifaHora) { this.tarifaHora = tarifaHora; }
    public BigDecimal getBruto() { return bruto; }
    public void setBruto(BigDecimal bruto) { this.bruto = bruto; }
    public BigDecimal getAdelantos() { return adelantos; }
    public void setAdelantos(BigDecimal adelantos) { this.adelantos = adelantos; }
    public BigDecimal getNeto() { return neto; }
    public void setNeto(BigDecimal neto) { this.neto = neto; }
}
