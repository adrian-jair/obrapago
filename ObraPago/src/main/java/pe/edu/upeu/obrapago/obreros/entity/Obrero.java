package pe.edu.upeu.obrapago.obreros.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "OBREROS")
public class Obrero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Oficio oficio;

    @Column(name = "TARIFA_JORNAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaJornal;

    @Column(length = 9)
    private String celular;

    @Column(nullable = false)
    private Boolean activo = true;

    @Lob
    @Column(name = "FOTO_URL")
    private String fotoUrl;

    public Obrero() {}

    public Obrero(Long id, String dni, String nombres, String apellidos, Oficio oficio,
                  BigDecimal tarifaJornal, String celular, Boolean activo, String fotoUrl) {
        this.id = id;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.oficio = oficio;
        this.tarifaJornal = tarifaJornal;
        this.celular = celular;
        this.activo = activo;
        this.fotoUrl = fotoUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public Oficio getOficio() { return oficio; }
    public void setOficio(Oficio oficio) { this.oficio = oficio; }
    public BigDecimal getTarifaJornal() { return tarifaJornal; }
    public void setTarifaJornal(BigDecimal tarifaJornal) { this.tarifaJornal = tarifaJornal; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getNombreCompleto() {
        String ap = apellidos != null ? apellidos.trim() : "";
        String nom = nombres != null ? nombres.trim() : "";
        if (ap.isEmpty()) return nom;
        if (nom.isEmpty()) return ap;
        return ap + ", " + nom;
    }
}