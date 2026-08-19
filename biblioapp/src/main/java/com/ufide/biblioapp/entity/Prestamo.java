package com.ufide.biblioapp.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

// un prestamo tiene un libro, un usuario y las fechas
@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // muchos prestamos pueden apuntar al mismo libro
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "libro_id")
    private Libro libro;

    // y al mismo usuario
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate fechaPrestamo;

    @Column(nullable = false)
    private LocalDate fechaLimite;

    private LocalDate fechaDevolucion; // queda null hasta que se devuelve

    public Prestamo() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    // devuelve true si ya tiene fecha de devolucion
    @Transient
    public boolean isDevuelto() {
        return fechaDevolucion != null;
    }
}
