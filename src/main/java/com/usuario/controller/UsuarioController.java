package com.usuario.controller;
import com.usuario.dto.LoginRequest;
import com.usuario.model.Usuario;
import com.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.usuario.dto.RecuperarRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return usuarioService.validarLogin(request.getEmail(), request.getPassword())
                .map(usuario -> ResponseEntity.ok(usuario)) // Devuelve código 200 y los datos
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()); // Devuelve código 401
    }

    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperar(@RequestBody RecuperarRequest request) {
        boolean exito = usuarioService.recuperarPassword(request.getEmail());
        
        Map<String, String> respuesta = new HashMap<>();
        
        if (exito) {
            respuesta.put("mensaje", "Contraseña temporal generada. Revisa la consola del servidor.");
            return ResponseEntity.ok(respuesta); // Código 200
        } else {
            respuesta.put("error", "El correo ingresado no existe en nuestros registros.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta); // Código 404
        }
    }
}