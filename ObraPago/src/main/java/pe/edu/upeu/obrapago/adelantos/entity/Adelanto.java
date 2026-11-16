package pe.edu.upeu.obrapago.adelantos.entity;

import jakarta.persistence.*;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ADELANTOS")
public class Adelanto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OBRERO_ID", nullable = false)
    private Obrero obrero;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importe;

    @Column(length = 250)
    private String concepto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAdelanto estado = EstadoAdelanto.REGISTRADO;

    @Column(name = "CREADO_EN", nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public Adelanto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Obrero getObrero() { return obrero; }
    public void setObrero(Obrero obrero) { this.obrero = obrero; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public EstadoAdelanto getEstado() { return estado; }
    public void setEstado(EstadoAdelanto estado) { this.estado = estado; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
