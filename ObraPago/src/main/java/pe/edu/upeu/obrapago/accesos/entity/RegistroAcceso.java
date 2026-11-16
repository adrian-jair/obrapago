package pe.edu.upeu.obrapago.accesos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCESOS")
public class RegistroAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(length = 30)
    private String rol;

    @Column(name = "IP_REMOTA", length = 50)
    private String ipRemota;

    @Column(length = 200)
    private String navegador;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(name = "FECHA_HORA", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean exito = true;

    @Column(length = 300)
    private String mensaje;

    public RegistroAcceso() {}

    public RegistroAcceso(Long id, String username, String rol, String ipRemota, String navegador,
                          String accion, LocalDateTime fechaHora, Boolean exito, String mensaje) {
        this.id = id;
        this.username = username;
        this.rol = rol;
        this.ipRemota = ipRemota;
        this.navegador = navegador;
        this.accion = accion;
        this.fechaHora = fechaHora != null ? fechaHora : LocalDateTime.now();
        this.exito = exito != null ? exito : true;
        this.mensaje = mensaje;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getIpRemota() { return ipRemota; }
    public void setIpRemota(String ipRemota) { this.ipRemota = ipRemota; }
    public String getNavegador() { return navegador; }
    public void setNavegador(String navegador) { this.navegador = navegador; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public Boolean getExito() { return exito; }
    public void setExito(Boolean exito) { this.exito = exito; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
