package com.ufide.biblioapp.service;

import com.ufide.biblioapp.dto.LibroRanking;
import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// aca va la logica de prestamos (no en el controller)
@Service
public class PrestamoService {

    private static final int DIAS_PLAZO = 14; // dias que dura un prestamo

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private LibroService libroService;

    public List<Prestamo> listarTodos() {
        return prestamoRepository.findAllConLibroYUsuario();
    }

    public List<Prestamo> listarPorUsuario(Usuario usuario) {
        return prestamoRepository.findByUsuario(usuario);
    }

    public List<Prestamo> listarAtrasados() {
        return prestamoRepository.prestamosAtrasados();
    }

    public List<LibroRanking> ranking() {
        return prestamoRepository.ranking();
    }

    public Prestamo buscarPorId(Long id) {
        return prestamoRepository.findById(id).orElse(null);
    }

    // @Transactional: guardar el prestamo y restar la copia van juntos
    @Transactional
    public Prestamo registrar(Libro libro, Usuario usuario) {
        // no se puede prestar si no quedan copias
        if (libro.getCopiasDisponibles() == null || libro.getCopiasDisponibles() <= 0) {
            throw new IllegalStateException("No hay copias disponibles de este libro.");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);

        // la fecha limite es hoy + 14 dias
        LocalDate hoy = LocalDate.now();
        prestamo.setFechaPrestamo(hoy);
        prestamo.setFechaLimite(hoy.plusDays(DIAS_PLAZO));
        prestamo.setFechaDevolucion(null); // aun no devuelto

        libroService.descontarCopia(libro);
        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public Prestamo devolver(Long prestamoId) {
        Prestamo prestamo = buscarPorId(prestamoId);
        if (prestamo == null) {
            throw new IllegalArgumentException("Prestamo no encontrado: " + prestamoId);
        }
        // si ya se devolvio no lo devuelvo de nuevo (sino sumaria copias de mas)
        if (prestamo.getFechaDevolucion() != null) {
            throw new IllegalStateException("Este prestamo ya fue devuelto.");
        }

        prestamo.setFechaDevolucion(LocalDate.now());
        libroService.devolverCopia(prestamo.getLibro());
        return prestamoRepository.save(prestamo);
    }
}
