package com.usuario.user.controller;

import com.usuario.model.Usuario;
import com.usuario.service.UsuarioService;
import com.usuario.controller.UsuarioController;
import com.usuario.dto.LoginRequest;
import com.usuario.dto.RecuperarRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
    })
    static class TestConfig {}

    // 1. Test Listar (Cubre el GET general)
    @Test
    void testListar_Success() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(List.of(new Usuario()));
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());
    }

    // 2. Test Obtener (Caso Exitoso y Caso 404)
    @Test
    void testObtener_NotFound() throws Exception {
        when(usuarioService.buscarPorId(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    // 3. Test Actualizar (Caso Exitoso y Caso 404)
    @Test
    void testActualizar_Success() throws Exception {
        Usuario user = new Usuario();
        user.setUsername("update");
        user.setEmail("update@test.com");
        user.setPassword("12345678");
        user.setRol("USER");

        when(usuarioService.buscarPorId(anyLong())).thenReturn(Optional.of(user));
        when(usuarioService.guardar(any(Usuario.class))).thenReturn(user);

        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    // 4. Test Borrar (Caso Exitoso y Caso 404)
    @Test
    void testBorrar_Success() throws Exception {
        when(usuarioService.buscarPorId(anyLong())).thenReturn(Optional.of(new Usuario()));
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testBorrar_NotFound() throws Exception {
        when(usuarioService.buscarPorId(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNotFound());
    }

    // 5. Test Login Fallido (Cubre el 401 UNAUTHORIZED)
    @Test
    void testLogin_Failed() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("error@test.com");
        login.setPassword("wrong");

        when(usuarioService.validarLogin(anyString(), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    // 6. Test Recuperar Fallido (Cubre el 404 del else)
    @Test
    void testRecuperar_Failed() throws Exception {
        RecuperarRequest req = new RecuperarRequest();
        req.setEmail("noexiste@test.com");

        when(usuarioService.recuperarPassword(anyString())).thenReturn(false);

        mockMvc.perform(post("/api/usuarios/recuperar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }
}