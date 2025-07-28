package com.f1news.backend.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

@Entity
@Table(name = "estudiantes")
public class Estudiante {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre completo es requerido")
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;
    
    @Email(message = "El correo debe tener un formato válido")
    @NotBlank(message = "El correo es requerido")
    @Column(unique = true, nullable = false)
    private String correo;
    
    @NotBlank(message = "El teléfono es requerido")
    private String telefono;
    
    @NotNull(message = "La fecha de nacimiento es requerida")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    
    @NotBlank(message = "La carrera es requerida")
    private String carrera;
    
    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEstudiante estado = EstadoEstudiante.ACTIVO;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.ESTUDIANTE;
    
    // Constructores
    public Estudiante() {
        this.estado = EstadoEstudiante.ACTIVO;
        this.rol = Rol.ESTUDIANTE;
    }
    
    public Estudiante(String nombreCompleto, String correo, String telefono, 
                     LocalDate fechaNacimiento, String carrera, String password) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.carrera = carrera;
        this.password = password;
        this.fotoUrl = null; // Por defecto sin foto
        this.estado = EstadoEstudiante.ACTIVO;
        this.rol = Rol.ESTUDIANTE;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public String getCarrera() {
        return carrera;
    }
    
    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }
    
    public String getFotoUrl() {
        return fotoUrl;
    }
    
    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
    
    public EstadoEstudiante getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoEstudiante estado) {
        this.estado = estado;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Rol getRol() {
        return rol;
    }
    
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
    public enum EstadoEstudiante {
        ACTIVO, INACTIVO
    }
    
    public enum Rol {
        ADMINISTRADOR, ESTUDIANTE
    }
}
