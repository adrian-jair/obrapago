package pe.edu.upeu.obrapago.asistencia.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "JORNADAS_ASISTENCIA",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_JORNADA_FECHA",
        columnNames = "FECHA"
    )
)
public class JornadaAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoJornada estado = EstadoJornada.ABIERTA;

    @OneToMany(
        mappedBy = "jornada",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<DetalleAsistencia> detalles = new ArrayList<>();

    public JornadaAsistencia() {
        this.estado = EstadoJornada.ABIERTA;
    }

    public JornadaAsistencia(
            Long id,
            LocalDate fecha,
            EstadoJornada estado,
            List<DetalleAsistencia> detalles) {

        this.id = id;
        this.fecha = fecha;

        this.estado = estado != null
                ? estado
                : EstadoJornada.ABIERTA;

        this.detalles = detalles != null
                ? detalles
                : new ArrayList<>();
    }

    public void agregarDetalle(DetalleAsistencia detalle) {
        if (detalle != null) {
            detalles.add(detalle);
            detalle.setJornada(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public void setEstado(EstadoJornada estado) {
        this.estado = estado;
    }

    public List<DetalleAsistencia> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleAsistencia> detalles) {
        this.detalles = detalles != null
                ? detalles
                : new ArrayList<>();
    }
}