package com.f1news.backend.integration;

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
import org.springframework.security.test.context.support.WithMockUser;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@DisplayName("Pruebas de Integración - API Endpoints")
public class IntegrationTest {

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
    }

    @Test
    @DisplayName("Test 1: Flujo completo - Login y operaciones con estudiantes")
    @WithMockUser(username = "admin@test.com", roles = {"ADMINISTRADOR"})
    void testFlujoCompletoEstudiantes() throws Exception {
        // 1. Crear un estudiante
        Map<String, Object> nuevoEstudiante = new HashMap<>();
        nuevoEstudiante.put("nombreCompleto", "Integration Test Student");
        nuevoEstudiante.put("correo", "integration@estudiante.edu");
        nuevoEstudiante.put("password", "password123");
        nuevoEstudiante.put("telefono", "1234567890");
        nuevoEstudiante.put("fechaNacimiento", "2000-01-01");
        nuevoEstudiante.put("carrera", "Ingeniería de Sistemas");

        // POST - Crear estudiante
        String response = mockMvc.perform(post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoEstudiante)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Integration Test Student"))
                .andExpect(jsonPath("$.correo").value("integration@estudiante.edu"))
                .andReturn().getResponse().getContentAsString();

        // Extraer ID del estudiante creado
        Map<String, Object> estudianteCreado = objectMapper.readValue(response, Map.class);
        Integer estudianteId = (Integer) estudianteCreado.get("id");

        // 2. GET - Obtener el estudiante creado
        mockMvc.perform(get("/api/estudiantes/" + estudianteId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Integration Test Student"));

        // 3. PUT - Actualizar el estudiante
        Map<String, Object> estudianteActualizado = new HashMap<>(nuevoEstudiante);
        estudianteActualizado.put("nombreCompleto", "Integration Test Student Updated");
        estudianteActualizado.put("telefono", "9876543210");

        mockMvc.perform(put("/api/estudiantes/" + estudianteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estudianteActualizado)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Integration Test Student Updated"))
                .andExpect(jsonPath("$.telefono").value("9876543210"));

        // 4. GET - Listar todos los estudiantes
        mockMvc.perform(get("/api/estudiantes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        // 5. DELETE - Eliminar el estudiante
        mockMvc.perform(delete("/api/estudiantes/" + estudianteId))
                .andDo(print())
                .andExpect(status().isOk());

        // 6. GET - Verificar que el estudiante fue eliminado
        mockMvc.perform(get("/api/estudiantes/" + estudianteId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test 2: Flujo completo - Operaciones con administradores")
    @WithMockUser(username = "admin@test.com", roles = {"ADMINISTRADOR"})
    void testFlujoCompletoAdministradores() throws Exception {
        // 1. Crear un administrador
        Map<String, Object> nuevoAdministrador = new HashMap<>();
        nuevoAdministrador.put("nombreCompleto", "Integration Test Admin");
        nuevoAdministrador.put("correo", "integration.admin@universidad.edu");
        nuevoAdministrador.put("password", "admin123");
        nuevoAdministrador.put("telefono", "0987654321");
        nuevoAdministrador.put("fechaNacimiento", "1980-01-01");

        // POST - Crear administrador
        String response = mockMvc.perform(post("/api/administradores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoAdministrador)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Integration Test Admin"))
                .andExpect(jsonPath("$.correo").value("integration.admin@universidad.edu"))
                .andReturn().getResponse().getContentAsString();

        // Extraer ID del administrador creado
        Map<String, Object> administradorCreado = objectMapper.readValue(response, Map.class);
        Integer administradorId = (Integer) administradorCreado.get("id");

        // 2. PUT - Actualizar el administrador (sin password)
        Map<String, Object> administradorActualizado = new HashMap<>();
        administradorActualizado.put("nombreCompleto", "Integration Test Admin Updated");
        administradorActualizado.put("correo", "integration.admin@universidad.edu");
        administradorActualizado.put("telefono", "5555555555");
        administradorActualizado.put("fechaNacimiento", "1980-01-01");
        administradorActualizado.put("estado", "ACTIVO");
        administradorActualizado.put("rol", "ADMINISTRADOR");

        mockMvc.perform(put("/api/administradores/" + administradorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(administradorActualizado)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Integration Test Admin Updated"))
                .andExpect(jsonPath("$.telefono").value("5555555555"));

        // 3. GET - Listar todos los administradores
        mockMvc.perform(get("/api/administradores"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Test 3: Pruebas de seguridad - Acceso sin autenticación")
    void testAccesoSinAutenticacion() throws Exception {
        // Intentar acceder a endpoints protegidos sin autenticación
        mockMvc.perform(get("/api/estudiantes"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/administradores"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test 4: Validación de errores - Datos inválidos")
    @WithMockUser(username = "admin@test.com", roles = {"ADMINISTRADOR"})
    void testValidacionErrores() throws Exception {
        // Intentar crear estudiante con datos faltantes
        Map<String, Object> estudianteInvalido = new HashMap<>();
        estudianteInvalido.put("correo", "invalido@test.com");
        // Faltan campos requeridos

        mockMvc.perform(post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estudianteInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test 5: Búsqueda y filtros")
    @WithMockUser(username = "admin@test.com", roles = {"ADMINISTRADOR"})
    void testBusquedaYFiltros() throws Exception {
        // Crear algunos estudiantes para probar búsqueda
        Estudiante estudiante1 = crearEstudianteEnBD("Juan Pérez", "juan@test.edu", "Sistemas");
        Estudiante estudiante2 = crearEstudianteEnBD("María González", "maria@test.edu", "Civil");
        Estudiante estudiante3 = crearEstudianteEnBD("Carlos Rodríguez", "carlos@test.edu", "Sistemas");

        // Probar endpoint de búsqueda si existe
        mockMvc.perform(get("/api/estudiantes")
                .param("carrera", "Sistemas"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // Método auxiliar para crear estudiante en BD
    private Estudiante crearEstudianteEnBD(String nombre, String correo, String carrera) {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombreCompleto(nombre);
        estudiante.setCorreo(correo);
        estudiante.setPassword(passwordEncoder.encode("password123"));
        estudiante.setTelefono("1234567890");
        estudiante.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        estudiante.setCarrera(carrera);
        estudiante.setEstado(Estudiante.EstadoEstudiante.ACTIVO);
        estudiante.setRol(Estudiante.Rol.ESTUDIANTE);
        return estudianteRepository.save(estudiante);
    }
}
