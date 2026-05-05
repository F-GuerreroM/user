package com.usuario.user.service;

import com.usuario.model.Usuario;
import com.usuario.repository.UsuarioRepository;
import com.usuario.service.UsuarioService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testCorrectoModuloService_Guardar() {
        Usuario usuario = new Usuario();
        usuario.setUsername("fguerrero");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.guardar(usuario);

        assertNotNull(resultado);
        assertEquals("fguerrero", resultado.getUsername());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testCorrectoModuloService_ListarTodos() {
        when(usuarioRepository.findAll()).thenReturn(List.of(new Usuario()));
        List<Usuario> resultado = usuarioService.listarTodos();
        assertFalse(resultado.isEmpty());
    }

    @Test
    void testCorrectoModuloService_ValidarLogin_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@duoc.cl");
        usuario.setPassword("clave1234");
        usuario.setUsername("usuarioTest");

      
        when(usuarioRepository.findByEmail("test@duoc.cl")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.validarLogin("test@duoc.cl", "clave1234");

        assertTrue(resultado.isPresent());
        assertEquals("usuarioTest", resultado.get().getUsername());
    }

    @Test
    void testCorrectoModuloService_ValidarLogin_Fallido() {
        Usuario usuario = new Usuario();
        usuario.setPassword("claveReal");

        when(usuarioRepository.findByEmail("test@duoc.cl")).thenReturn(Optional.of(usuario));

       
        Optional<Usuario> resultado = usuarioService.validarLogin("test@duoc.cl", "claveErronea");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void testCorrectoModuloService_RecuperarPassword_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@duoc.cl");

        when(usuarioRepository.findByEmail("test@duoc.cl")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        boolean resultado = usuarioService.recuperarPassword("test@duoc.cl");

        assertTrue(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testCorrectoModuloService_RecuperarPassword_NoExiste() {
        when(usuarioRepository.findByEmail("noexiste@duoc.cl")).thenReturn(Optional.empty());

        boolean resultado = usuarioService.recuperarPassword("noexiste@duoc.cl");

        assertFalse(resultado);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}