package com.usuario.service;

import com.usuario.model.Usuario;
import com.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

    public Optional<Usuario> buscarPorId(Long id) {
        log.debug("Buscando ID: {}", id);
        return usuarioRepository.findById(id);
    }

    public void eliminar(Long id) {
        log.warn("Eliminando ID: {}", id);
        usuarioRepository.deleteById(id);
    }
}