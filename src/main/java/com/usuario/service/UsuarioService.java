package com.usuario.service;

import com.usuario.model.Usuario;
import com.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario guardar(Usuario usuario) {
        log.info("Guardando usuario: {}", usuario.getUsername());
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        log.info("Consultando todos los usuarios");
        return usuarioRepository.findAll();
    }

    @SuppressWarnings("null")
    public Optional<Usuario> buscarPorId(Long id) {
        log.debug("Buscando ID: {}", id);
        return usuarioRepository.findById(id);
    }

    @SuppressWarnings("null")
    public void eliminar(Long id) {
        log.warn("Eliminando ID: {}", id);
        usuarioRepository.deleteById(id);
    }
    public Optional<Usuario> validarLogin(String email, String password) {
    log.info("Validando intento de login para el correo: {}", email);
    
    Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
    
    // Si el usuario existe y la contraseña coincide
    if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
        log.info("Login exitoso para el usuario: {}", usuario.get().getUsername());
        return usuario;
    }
    
    log.warn("Credenciales inválidas para el correo: {}", email);
    return Optional.empty();
    }

    public boolean recuperarPassword(String email) {
        log.info("Solicitando recuperación de contraseña para: {}", email);
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Genera un texto aleatorio y corta los primeros 8 caracteres
            String passwordTemporal = UUID.randomUUID().toString().substring(0, 8);
            
            // Sobreescribe la contraseña y guarda en la BD
            usuario.setPassword(passwordTemporal);
            usuarioRepository.save(usuario);
            
            // ¡ATENCIÓN! En un entorno real aquí se enviaría un correo (JavaMailSender).
            // Para tu laboratorio, la imprimimos en consola para que la copies y pruebes.
            log.info("=====================================================");
            log.info("ÉXITO: La nueva clave temporal para {} es: {}", email, passwordTemporal);
            log.info("=====================================================");
            
            return true;
        }
        
        log.warn("El correo {} no existe en la base de datos.", email);
        return false;
    }

}