package com.ufide.biblioapp.service;

import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

// implementa UserDetailsService: asi Spring Security sabe como cargar los usuarios
@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // busca el usuario por username y le arma el rol con el prefijo ROLE_
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())))
                .build();
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElse(null);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // al crear un usuario guardo la password hasheada con BCrypt, nunca en texto plano
    public Usuario crear(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario datos, String passwordPlano) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));

        existente.setUsername(datos.getUsername());
        existente.setNombreCompleto(datos.getNombreCompleto());
        existente.setEmail(datos.getEmail());
        existente.setRol(datos.getRol());

        if (passwordPlano != null && !passwordPlano.isBlank()) {
            existente.setPassword(passwordEncoder.encode(passwordPlano));
        }
        return usuarioRepository.save(existente);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public void actualizarPassword(Usuario usuario, String nuevoPlano) {
        usuario.setPassword(passwordEncoder.encode(nuevoPlano));
        usuarioRepository.save(usuario);
    }
}
