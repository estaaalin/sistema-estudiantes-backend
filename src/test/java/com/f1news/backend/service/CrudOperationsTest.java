package com.f1news.backend.service;

import com.f1news.backend.config.TestConfig;
import com.f1news.backend.model.Administrador;
import com.f1news.backend.model.Estudiante;
import com.f1news.backend.repository.AdministradorRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@DisplayName("Pruebas CRUD - Operaciones Básicas")
public class CrudOperationsTest {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @BeforeEach
    void setUp() {
        estudianteRepository.deleteAll();
        administradorRepository.deleteAll();
    }

    // ==================== PRUEBAS CREATE ====================

    @Test
    @DisplayName("Test 1: CREATE - Crear estudiante exitosamente")
    void testCreateEstudiante() {
        Estudiante estudiante = crearEstudiantePrueba("Ana Martínez", "ana.martinez@estudiante.edu");

        Estudiante resultado = estudianteService.createEstudiante(estudiante);

        assertNotNull(resultado.getId());
        assertEquals("Ana Martínez", resultado.getNombreCompleto());
        assertEquals("ana.martinez@estudiante.edu", resultado.getCorreo());
        assertEquals(Estudiante.EstadoEstudiante.ACTIVO, resultado.getEstado());
        assertEquals(Estudiante.Rol.ESTUDIANTE, resultado.getRol());
    }

    @Test
    @DisplayName("Test 2: CREATE - Crear administrador exitosamente")
    void testCreateAdministrador() {
        Administrador administrador = crearAdministradorPrueba("Director Test", "director.test@universidad.edu");

        Administrador resultado = administradorService.createAdministrador(administrador);

        assertNotNull(resultado.getId());
        assertEquals("Director Test", resultado.getNombreCompleto());
        assertEquals("director.test@universidad.edu", resultado.getCorreo());
        assertEquals(Administrador.EstadoAdministrador.ACTIVO, resultado.getEstado());
        assertEquals(Administrador.Rol.ADMINISTRADOR, resultado.getRol());
    }

    // ==================== PRUEBAS READ ====================

    @Test
    @DisplayName("Test 3: READ - Obtener estudiante por ID")
    void testReadEstudiantePorId() {
        Estudiante estudiante = crearEstudiantePrueba("Carlos López", "carlos.lopez@estudiante.edu");
        Estudiante estudianteCreado = estudianteService.createEstudiante(estudiante);

        Optional<Estudiante> resultado = estudianteService.getEstudianteById(estudianteCreado.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Carlos López", resultado.get().getNombreCompleto());
        assertEquals("carlos.lopez@estudiante.edu", resultado.get().getCorreo());
    }

    @Test
    @DisplayName("Test 4: READ - Obtener todos los estudiantes")
    void testReadTodosLosEstudiantes() {
        // Crear múltiples estudiantes
        estudianteService.createEstudiante(crearEstudiantePrueba("Estudiante 1", "est1@test.edu"));
        estudianteService.createEstudiante(crearEstudiantePrueba("Estudiante 2", "est2@test.edu"));
        estudianteService.createEstudiante(crearEstudiantePrueba("Estudiante 3", "est3@test.edu"));

        List<Estudiante> estudiantes = estudianteService.getAllEstudiantes();

        assertEquals(3, estudiantes.size());
    }

    @Test
    @DisplayName("Test 5: READ - Buscar estudiante por correo")
    void testReadEstudiantePorCorreo() {
        Estudiante estudiante = crearEstudiantePrueba("María Rodríguez", "maria.rodriguez@estudiante.edu");
        estudianteService.createEstudiante(estudiante);

        Optional<Estudiante> resultado = estudianteService.getEstudianteByCorreo("maria.rodriguez@estudiante.edu");

        assertTrue(resultado.isPresent());
        assertEquals("María Rodríguez", resultado.get().getNombreCompleto());
    }

    // ==================== PRUEBAS UPDATE ====================

    @Test
    @DisplayName("Test 6: UPDATE - Actualizar estudiante exitosamente")
    void testUpdateEstudiante() {
        // Crear estudiante inicial
        Estudiante estudiante = crearEstudiantePrueba("Pedro Gómez", "pedro.gomez@estudiante.edu");
        Estudiante estudianteCreado = estudianteService.createEstudiante(estudiante);

        // Preparar datos para actualización
        Estudiante datosActualizacion = new Estudiante();
        datosActualizacion.setNombreCompleto("Pedro Gómez Actualizado");
        datosActualizacion.setCorreo("pedro.gomez@estudiante.edu");
        datosActualizacion.setTelefono("9999999999");
        datosActualizacion.setFechaNacimiento(LocalDate.of(1995, 6, 15));
        datosActualizacion.setCarrera("Ingeniería Actualizada");
        datosActualizacion.setEstado(Estudiante.EstadoEstudiante.INACTIVO);
        datosActualizacion.setRol(Estudiante.Rol.ESTUDIANTE);

        // Actualizar
        Estudiante resultado = estudianteService.updateEstudiante(estudianteCreado.getId(), datosActualizacion);

        assertEquals("Pedro Gómez Actualizado", resultado.getNombreCompleto());
        assertEquals("9999999999", resultado.getTelefono());
        assertEquals("Ingeniería Actualizada", resultado.getCarrera());
        assertEquals(Estudiante.EstadoEstudiante.INACTIVO, resultado.getEstado());
    }

    @Test
    @DisplayName("Test 7: UPDATE - Actualizar administrador sin password")
    void testUpdateAdministradorSinPassword() {
        // Crear administrador inicial
        Administrador administrador = crearAdministradorPrueba("Admin Original", "admin.original@universidad.edu");
        Administrador administradorCreado = administradorService.createAdministrador(administrador);

        // Preparar datos para actualización (sin password)
        Administrador datosActualizacion = new Administrador();
        datosActualizacion.setNombreCompleto("Admin Actualizado");
        datosActualizacion.setCorreo("admin.original@universidad.edu");
        datosActualizacion.setTelefono("8888888888");
        datosActualizacion.setFechaNacimiento(LocalDate.of(1975, 3, 10));
        datosActualizacion.setEstado(Administrador.EstadoAdministrador.ACTIVO);
        datosActualizacion.setRol(Administrador.Rol.ADMINISTRADOR);
        // No se establece password

        // Actualizar
        Administrador resultado = administradorService.updateAdministrador(administradorCreado.getId(), datosActualizacion);

        assertEquals("Admin Actualizado", resultado.getNombreCompleto());
        assertEquals("8888888888", resultado.getTelefono());
        // El password original debe mantenerse
        assertNotNull(resultado.getPassword());
    }

    // ==================== PRUEBAS DELETE ====================

    @Test
    @DisplayName("Test 8: DELETE - Eliminar estudiante exitosamente")
    void testDeleteEstudiante() {
        // Crear estudiante
        Estudiante estudiante = crearEstudiantePrueba("Estudiante a Eliminar", "eliminar@test.edu");
        Estudiante estudianteCreado = estudianteService.createEstudiante(estudiante);

        // Verificar que existe
        assertTrue(estudianteService.getEstudianteById(estudianteCreado.getId()).isPresent());

        // Eliminar
        assertDoesNotThrow(() -> {
            estudianteService.deleteEstudiante(estudianteCreado.getId());
        });

        // Verificar que no existe
        assertFalse(estudianteService.getEstudianteById(estudianteCreado.getId()).isPresent());
    }

    @Test
    @DisplayName("Test 9: DELETE - Eliminar administrador exitosamente")
    void testDeleteAdministrador() {
        // Crear administrador
        Administrador administrador = crearAdministradorPrueba("Admin a Eliminar", "admin.eliminar@universidad.edu");
        Administrador administradorCreado = administradorService.createAdministrador(administrador);

        // Verificar que existe
        assertTrue(administradorService.getAdministradorById(administradorCreado.getId()).isPresent());

        // Eliminar
        assertDoesNotThrow(() -> {
            administradorService.deleteAdministrador(administradorCreado.getId());
        });

        // Verificar que no existe
        assertFalse(administradorService.getAdministradorById(administradorCreado.getId()).isPresent());
    }

    @Test
    @DisplayName("Test 10: DELETE - Error al eliminar elemento inexistente")
    void testDeleteElementoInexistente() {
        Long idInexistente = 99999L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            estudianteService.deleteEstudiante(idInexistente);
        });

        assertTrue(exception.getMessage().contains("no encontrado"));
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Estudiante crearEstudiantePrueba(String nombre, String correo) {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombreCompleto(nombre);
        estudiante.setCorreo(correo);
        estudiante.setPassword("password123");
        estudiante.setTelefono("1234567890");
        estudiante.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        estudiante.setCarrera("Ingeniería de Sistemas");
        return estudiante;
    }

    private Administrador crearAdministradorPrueba(String nombre, String correo) {
        Administrador administrador = new Administrador();
        administrador.setNombreCompleto(nombre);
        administrador.setCorreo(correo);
        administrador.setPassword("admin123");
        administrador.setTelefono("0987654321");
        administrador.setFechaNacimiento(LocalDate.of(1980, 1, 1));
        return administrador;
    }
}
