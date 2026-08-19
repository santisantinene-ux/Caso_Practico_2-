package com.ufide.biblioapp.repository;

import com.ufide.biblioapp.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // busca el token que viene en el link del correo
    Optional<PasswordResetToken> findByToken(String token);
}
