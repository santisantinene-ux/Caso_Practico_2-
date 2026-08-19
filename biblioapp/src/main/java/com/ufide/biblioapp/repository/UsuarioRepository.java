package com.ufide.biblioapp.repository;

import com.ufide.biblioapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // para el login (busca por nombre de usuario)
    Optional<Usuario> findByUsername(String username);

    // para recuperar la contrasena (busca por correo)
    Optional<Usuario> findByEmail(String email);
}
