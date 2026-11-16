package pe.edu.upeu.obrapago.asistencia.entity;

import jakarta.persistence.*;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;
import java.math.BigDecimal;

@Entity
@Table(name = "DETALLES_ASISTENCIA",
       uniqueConstraints = @UniqueConstraint(name = "UK_JORNADA_OBRERO", columnNames = {"JORNADA_ID", "OBRERO_ID"}))
public class DetalleAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "JORNADA_ID", nullable = false)
    private JornadaAsistencia jornada;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OBRERO_ID", nullable = false)
    private Obrero obrero;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_ASISTENCIA", nullable = false, length = 30)
    private EstadoAsistencia estadoAsistencia;

    @Column(name = "HORAS_EXTRA", nullable = false, precision = 6, scale = 2)
    private BigDecimal horasExtra;

    @Column(length = 250)
    private String observacion;

    @Lob
    @Column(name = "DOCUMENTO_URL")
    private String documentoUrl;

    public DetalleAsistencia() {}

    public DetalleAsistencia(Long id, JornadaAsistencia jornada, Obrero obrero,
                             EstadoAsistencia estadoAsistencia, BigDecimal horasExtra, 
                             String observacion, String documentoUrl) {
        this.id = id;
        this.jornada = jornada;
        this.obrero = obrero;
        this.estadoAsistencia = estadoAsistencia;
        this.horasExtra = horasExtra;
        this.observacion = observacion;
        this.documentoUrl = documentoUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public JornadaAsistencia getJornada() { return jornada; }
    public void setJornada(JornadaAsistencia jornada) { this.jornada = jornada; }
    public Obrero getObrero() { return obrero; }
    public void setObrero(Obrero obrero) { this.obrero = obrero; }
    public EstadoAsistencia getEstadoAsistencia() { return estadoAsistencia; }
    public void setEstadoAsistencia(EstadoAsistencia estadoAsistencia) { this.estadoAsistencia = estadoAsistencia; }
    public BigDecimal getHorasExtra() { return horasExtra; }
    public void setHorasExtra(BigDecimal horasExtra) { this.horasExtra = horasExtra; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getDocumentoUrl() { return documentoUrl; }
    public void setDocumentoUrl(String documentoUrl) { this.documentoUrl = documentoUrl; }
}