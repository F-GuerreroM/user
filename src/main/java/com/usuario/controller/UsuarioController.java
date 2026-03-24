package com.usuario.controller;

import com.usuario.model.Usuario;
import com.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.guardar(usuario), HttpStatus.CREATED); // 201
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos()); // 200
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok) // Si existe, 200 OK
                .orElse(ResponseEntity.notFound().build()); // Si no, 404 Not Found
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario detalles) {
        return usuarioService.buscarPorId(id).map(u -> {
            u.setUsername(detalles.getUsername());
            u.setEmail(detalles.getEmail());
            u.setPassword(detalles.getPassword());
            u.setRol(detalles.getRol());
            return ResponseEntity.ok(usuarioService.guardar(u));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (usuarioService.buscarPorId(id).isPresent()) {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.notFound().build(); // 404
    }
}