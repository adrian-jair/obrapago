package pe.edu.upeu.obrapago.contratos.entity;

import jakarta.persistence.*;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONTRATOS")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OBRERO_ID", nullable = false)
    private Obrero obrero;

    @Column(name = "CODIGO_CONTRATO", nullable = false, unique = true, length = 30)
    private String codigoContrato;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CONTRATO", nullable = false, length = 30)
    private TipoContrato tipoContrato;

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Column(name = "TARIFA_PACTADA", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaPactada;

    @Column(nullable = false, length = 100)
    private String puesto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoContrato estado = EstadoContrato.ACTIVO;

    @Column(length = 300)
    private String observaciones;

    @Column(name = "CREADO_EN", nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public Contrato() {}

    public Contrato(Long id, Obrero obrero, String codigoContrato, TipoContrato tipoContrato,
                    LocalDate fechaInicio, LocalDate fechaFin, BigDecimal tarifaPactada,
                    String puesto, EstadoContrato estado, String observaciones) {
        this.id = id;
        this.obrero = obrero;
        this.codigoContrato = codigoContrato;
        this.tipoContrato = tipoContrato;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tarifaPactada = tarifaPactada;
        this.puesto = puesto;
        this.estado = estado != null ? estado : EstadoContrato.ACTIVO;
        this.observaciones = observaciones;
        this.creadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Obrero getObrero() { return obrero; }
    public void setObrero(Obrero obrero) { this.obrero = obrero; }
    public String getCodigoContrato() { return codigoContrato; }
    public void setCodigoContrato(String codigoContrato) { this.codigoContrato = codigoContrato; }
    public TipoContrato getTipoContrato() { return tipoContrato; }
    public void setTipoContrato(TipoContrato tipoContrato) { this.tipoContrato = tipoContrato; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public BigDecimal getTarifaPactada() { return tarifaPactada; }
    public void setTarifaPactada(BigDecimal tarifaPactada) { this.tarifaPactada = tarifaPactada; }
    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }
    public EstadoContrato getEstado() { return estado; }
    public void setEstado(EstadoContrato estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
