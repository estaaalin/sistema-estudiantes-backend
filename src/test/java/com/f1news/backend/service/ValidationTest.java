package com.f1news.backend.service;

import com.f1news.backend.config.TestConfig;
import com.f1news.backend.model.Estudiante;
import com.f1news.backend.repository.EstudianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@DisplayName("Pruebas de Validación")
public class ValidationTest {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @BeforeEach
    void setUp() {
        estudianteRepository.deleteAll();
    }

    @Test
    @DisplayName("Test 1: Validación exitosa - crear estudiante con datos válidos")
    void testValidacionExitosaEstudiante() {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombreCompleto("María García");
        estudiante.setCorreo("maria.garcia@estudiante.edu");
        estudiante.setPassword("password123");
        estudiante.setTelefono("1234567890");
        estudiante.setFechaNacimiento(LocalDate.of(1999, 5, 15));
        estudiante.setCarrera("Ingeniería Civil");

        assertDoesNotThrow(() -> {
            Estudiante resultado = estudianteService.createEstudiante(estudiante);
            assertNotNull(resultado.getId());
            assertEquals("María García", resultado.getNombreCompleto());
            assertEquals("maria.garcia@estudiante.edu", resultado.getCorreo());
            assertEquals(Estudiante.EstadoEstudiante.ACTIVO, resultado.getEstado());
        });
    }

    @Test
    @DisplayName("Test 2: Validación fallida - correo duplicado")
    void testValidacionCorreoDuplicado() {
        // Crear primer estudiante
        Estudiante estudiante1 = new Estudiante();
        estudiante1.setNombreCompleto("Juan Pérez");
        estudiante1.setCorreo("juan.perez@estudiante.edu");
        estudiante1.setPassword("password123");
        estudiante1.setTelefono("1234567890");
        estudiante1.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        estudiante1.setCarrera("Sistemas");

        estudianteService.createEstudiante(estudiante1);

        // Intentar crear segundo estudiante con mismo correo
        Estudiante estudiante2 = new Estudiante();
        estudiante2.setNombreCompleto("Pedro González");
        estudiante2.setCorreo("juan.perez@estudiante.edu"); // Mismo correo
        estudiante2.setPassword("password456");
        estudiante2.setTelefono("0987654321");
        estudiante2.setFechaNacimiento(LocalDate.of(1998, 12, 25));
        estudiante2.setCarrera("Civil");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            estudianteService.createEstudiante(estudiante2);
        });

        assertTrue(exception.getMessage().contains("Ya existe un estudiante con ese correo"));
    }

    @Test
    @DisplayName("Test 3: Validación fallida - datos requeridos faltantes")
    void testValidacionDatosFaltantes() {
        Estudiante estudiante = new Estudiante();
        // No se establecen campos requeridos
        estudiante.setCorreo("test@test.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            estudianteService.createEstudiante(estudiante);
        });

        // La excepción debería indicar que faltan datos requeridos
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Test 4: Validación de formato de correo")
    void testValidacionFormatoCorreo() {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombreCompleto("Ana López");
        estudiante.setCorreo("correo-invalido"); // Formato inválido
        estudiante.setPassword("password123");
        estudiante.setTelefono("1234567890");
        estudiante.setFechaNacimiento(LocalDate.of(1999, 3, 10));
        estudiante.setCarrera("Arquitectura");

        // El formato de correo debe causar un error de validación
        assertThrows(Exception.class, () -> {
            estudianteService.createEstudiante(estudiante);
        }, "Debería lanzar excepción por formato de correo inválido");
    }

    @Test
    @DisplayName("Test 5: Validación de actualización - mantener datos existentes")
    void testValidacionActualizacion() {
        // Crear estudiante inicial
        Estudiante estudiante = new Estudiante();
        estudiante.setNombreCompleto("Carlos Ruiz");
        estudiante.setCorreo("carlos.ruiz@estudiante.edu");
        estudiante.setPassword("password123");
        estudiante.setTelefono("1234567890");
        estudiante.setFechaNacimiento(LocalDate.of(1997, 8, 20));
        estudiante.setCarrera("Medicina");

        Estudiante estudianteCreado = estudianteService.createEstudiante(estudiante);

        // Actualizar solo algunos campos
        Estudiante estudianteActualizado = new Estudiante();
        estudianteActualizado.setNombreCompleto("Carlos Ruiz Actualizado");
        estudianteActualizado.setCorreo("carlos.ruiz@estudiante.edu");
        estudianteActualizado.setTelefono("0987654321");
        estudianteActualizado.setFechaNacimiento(LocalDate.of(1997, 8, 20));
        estudianteActualizado.setCarrera("Medicina");
        estudianteActualizado.setEstado(Estudiante.EstadoEstudiante.ACTIVO);
        estudianteActualizado.setRol(Estudiante.Rol.ESTUDIANTE);

        assertDoesNotThrow(() -> {
            Estudiante resultado = estudianteService.updateEstudiante(estudianteCreado.getId(), estudianteActualizado);
            assertEquals("Carlos Ruiz Actualizado", resultado.getNombreCompleto());
            assertEquals("0987654321", resultado.getTelefono());
        });
    }
}
