package com.ufide.biblioapp.service;

import com.ufide.biblioapp.entity.PasswordResetToken;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

// logica de "olvide mi contrasena" (bonus)
@Service
public class PasswordResetService {

    private static final long HORAS_VALIDEZ = 1; // el token dura 1 hora

    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioService usuarioService;
    private final EmailService emailService;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UsuarioService usuarioService,
                                EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    @Transactional
    public void solicitarRecuperacion(String email, String baseUrl) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario == null) {
            return; // si el correo no existe no digo nada, para no filtrar cuentas
        }
        // genero un token aleatorio con UUID
        String token = UUID.randomUUID().toString();
        tokenRepository.save(new PasswordResetToken(
                token, usuario, LocalDateTime.now().plusHours(HORAS_VALIDEZ)));

        String link = baseUrl + "/reset?token=" + token;
        emailService.enviarLinkRecuperacion(email, link);
    }

    public boolean tokenValido(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isInvalido())
                .isPresent();
    }

    @Transactional
    public boolean restablecer(String token, String nuevoPassword) {
        Optional<PasswordResetToken> encontrado = tokenRepository.findByToken(token)
                .filter(t -> !t.isInvalido());
        if (encontrado.isEmpty()) {
            return false;
        }
        PasswordResetToken prt = encontrado.get();
        usuarioService.actualizarPassword(prt.getUsuario(), nuevoPassword);
        prt.setUsado(true);
        tokenRepository.save(prt);
        return true;
    }
}
