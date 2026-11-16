package pe.edu.upeu.obrapago.obras.entity;

import jakarta.persistence.*;
import pe.edu.upeu.obrapago.obreros.entity.Obrero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "OBRAS",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_OBRA_CODIGO",
        columnNames = "CODIGO"
    )
)
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 200)
    private String ubicacion;

    @Column(length = 100)
    private String cliente;

    @Column(precision = 14, scale = 2)
    private BigDecimal presupuesto;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoObra estado = EstadoObra.EN_EJECUCION;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "CREADO_EN", nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    /**
     * Relación Muchos a Muchos (N:M):
     * Una Obra tiene asignados muchos Obreros,
     * y un Obrero puede pertenecer a muchas Obras.
     * Tabla asociativa intermedia: OBRA_OBREROS
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "OBRA_OBREROS",
        joinColumns = @JoinColumn(name = "OBRA_ID", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "OBRERO_ID", nullable = false),
        uniqueConstraints = @UniqueConstraint(
            name = "UK_OBRA_OBRERO",
            columnNames = {"OBRA_ID", "OBRERO_ID"}
        )
    )
    private Set<Obrero> obreros = new HashSet<>();

    public Obra() {}

    public Obra(Long id, String codigo, String nombre, String ubicacion, String cliente,
                BigDecimal presupuesto, LocalDate fechaInicio, LocalDate fechaFin,
                EstadoObra estado, String descripcion) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.cliente = cliente;
        this.presupuesto = presupuesto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado != null ? estado : EstadoObra.EN_EJECUCION;
        this.descripcion = descripcion;
        this.creadoEn = LocalDateTime.now();
    }

    public void agregarObrero(Obrero obrero) {
        if (obrero != null) {
            this.obreros.add(obrero);
        }
    }

    public void removerObrero(Obrero obrero) {
        if (obrero != null) {
            this.obreros.remove(obrero);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public BigDecimal getPresupuesto() { return presupuesto; }
    public void setPresupuesto(BigDecimal presupuesto) { this.presupuesto = presupuesto; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public EstadoObra getEstado() { return estado; }
    public void setEstado(EstadoObra estado) { this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public Set<Obrero> getObreros() { return obreros; }
    public void setObreros(Set<Obrero> obreros) { this.obreros = obreros; }
}
