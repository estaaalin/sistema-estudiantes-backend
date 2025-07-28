package com.f1news.backend.controller;

import com.f1news.backend.config.TestConfig;
import com.f1news.backend.model.Administrador;
import com.f1news.backend.model.Estudiante;
import com.f1news.backend.repository.AdministradorRepository;
import com.f1news.backend.repository.EstudianteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@DisplayName("Pruebas de Autenticación (Login)")
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Limpiar repositorios
        estudianteRepository.deleteAll();
        administradorRepository.deleteAll();

        // Crear datos de prueba
        crearDatosPrueba();
    }

    private void crearDatosPrueba() {
        // Crear estudiante de prueba
        Estudiante estudiante = new Estudiante();
        estudiante.setNombreCompleto("Juan Pérez Test");
        estudiante.setCorreo("juan.test@estudiante.edu");
        estudiante.setPassword(passwordEncoder.encode("password123"));
        estudiante.setTelefono("1234567890");
        estudiante.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        estudiante.setCarrera("Ingeniería de Sistemas");
        estudiante.setEstado(Estudiante.EstadoEstudiante.ACTIVO);
        estudiante.setRol(Estudiante.Rol.ESTUDIANTE);
        estudianteRepository.save(estudiante);

        // Crear administrador de prueba
        Administrador administrador = new Administrador();
        administrador.setNombreCompleto("Admin Test");
        administrador.setCorreo("admin.test@universidad.edu");
        administrador.setPassword(passwordEncoder.encode("admin123"));
        administrador.setTelefono("0987654321");
        administrador.setFechaNacimiento(LocalDate.of(1980, 1, 1));
        administrador.setEstado(Administrador.EstadoAdministrador.ACTIVO);
        administrador.setRol(Administrador.Rol.ADMINISTRADOR);
        administradorRepository.save(administrador);
    }

    @Test
    @DisplayName("Test 1: Login exitoso con estudiante")
    void testLoginEstudianteExitoso() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("correo", "juan.test@estudiante.edu");
        loginRequest.put("password", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.correo").value("juan.test@estudiante.edu"))
                .andExpect(jsonPath("$.user.rol").value("ESTUDIANTE"));
    }

    @Test
    @DisplayName("Test 2: Login exitoso con administrador")
    void testLoginAdministradorExitoso() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("correo", "admin.test@universidad.edu");
        loginRequest.put("password", "admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.correo").value("admin.test@universidad.edu"))
                .andExpect(jsonPath("$.user.rol").value("ADMINISTRADOR"));
    }

    @Test
    @DisplayName("Test 3: Login fallido - credenciales incorrectas")
    void testLoginCredencialesIncorrectas() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("correo", "juan.test@estudiante.edu");
        loginRequest.put("password", "passwordIncorrecto");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Test 4: Login fallido - usuario no existe")
    void testLoginUsuarioNoExiste() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("correo", "noexiste@test.com");
        loginRequest.put("password", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Test 5: Login fallido - datos faltantes")
    void testLoginDatosFaltantes() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("correo", "juan.test@estudiante.edu");
        // Sin password

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
