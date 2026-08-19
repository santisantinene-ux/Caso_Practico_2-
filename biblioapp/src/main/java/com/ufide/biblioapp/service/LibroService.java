package com.ufide.biblioapp.service;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public List<Libro> listar() {
        return libroRepository.findAll();
    }

    public List<Libro> listarPorCategoria(String categoria) {
        return libroRepository.findByCategoriaIgnoreCase(categoria);
    }

    public Optional<Libro> buscarPorId(Long id) {
        return libroRepository.findById(id);
    }

    public Libro guardar(Libro libro) {
        return libroRepository.save(libro);
    }

    // resta una copia (al prestar)
    public void descontarCopia(Libro libro) {
        libro.setCopiasDisponibles(libro.getCopiasDisponibles() - 1);
        libroRepository.save(libro);
    }

    // suma una copia (al devolver)
    public void devolverCopia(Libro libro) {
        libro.setCopiasDisponibles(libro.getCopiasDisponibles() + 1);
        libroRepository.save(libro);
    }
}
