package edu.tecnm.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String nombre;

    @Column(length = 100, unique = true) // Coincide con constraint UNIQUE
    private String email;

    @Column(length = 100)
    private String password;

    @Column(length = 45, unique = true) // Coincide con constraint UNIQUE
    private String username;

    private Integer estatus;

    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;

    // Relación Many-to-Many
    // JPA manejará la tabla "UsuarioPerfil"
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "UsuarioPerfil",
               joinColumns = @JoinColumn(name = "idusuario"),
               inverseJoinColumns = @JoinColumn(name = "idperfil")
    )
    private List<Perfil> perfiles;

    
    public Usuario() {
        this.fechaRegistro = new Date();
        this.estatus = 1;
        this.perfiles = new LinkedList<>();
    }
    
    // Método helper para agregar perfiles fácilmente
    public void agregarPerfil(Perfil tempPerfil) {
        if (perfiles == null) {
            perfiles = new LinkedList<>();
        }
        perfiles.add(tempPerfil);
    }
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getEstatus() {
        return estatus;
    }

    public void setEstatus(Integer estatus) {
        this.estatus = estatus;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<Perfil> getPerfiles() {
        return perfiles;
    }

    public void setPerfiles(List<Perfil> perfiles) {
        this.perfiles = perfiles;
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nombre=" + nombre + ", username=" + username + "]";
    }
}