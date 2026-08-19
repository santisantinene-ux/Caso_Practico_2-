package com.ufide.biblioapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    // @JsonIgnore para que la password nunca salga en el JSON de la API
    @JsonIgnore
    @NotBlank
    private String password;

    @NotBlank
    private String nombreCompleto;

    private String email;

    @NotBlank
    private String rol;

    // antes de guardar, chequeo que el rol sea uno valido del enum
    @PrePersist
    @PreUpdate
    private void validarRol() {
        if (!Rol.esValido(rol)) {
            throw new IllegalArgumentException(
                    "Rol invalido: '" + rol + "'. Debe ser " + Rol.BIBLIOTECARIO.name()
                            + " o " + Rol.LECTOR.name() + ".");
        }
    }

    public Usuario() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
