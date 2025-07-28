package com.f1news.backend;

import com.f1news.backend.model.Administrador;
import com.f1news.backend.model.Estudiante;
import com.f1news.backend.service.AdministradorService;
import com.f1news.backend.service.EstudianteService;
import com.f1news.backend.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SimpleAuthTest {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private Estudiante testEstudiante;
    private Administrador testAdministrador;

    @BeforeEach
    void setUp() {
        // Crear estudiante de prueba
        testEstudiante = new Estudiante();
        testEstudiante.setNombreCompleto("Juan Test");
        testEstudiante.setCorreo("juan.test@estudiante.edu");
        testEstudiante.setPassword("test123");
        testEstudiante.setRol(Estudiante.Rol.ESTUDIANTE);
        testEstudiante.setEstado(Estudiante.EstadoEstudiante.ACTIVO);
        testEstudiante.setCarrera("Ingeniería");
        testEstudiante.setTelefono("1234567890");
        testEstudiante.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        testEstudiante = estudianteService.createEstudiante(testEstudiante);

        // Crear administrador de prueba
        testAdministrador = new Administrador();
        testAdministrador.setNombreCompleto("Admin Test");
        testAdministrador.setCorreo("admin.test@universidad.edu");
        testAdministrador.setPassword("admin123");
        testAdministrador.setRol(Administrador.Rol.ADMINISTRADOR);
        testAdministrador.setEstado(Administrador.EstadoAdministrador.ACTIVO);
        testAdministrador.setTelefono("0987654321");
        testAdministrador.setFechaNacimiento(LocalDate.of(1980, 1, 1));
        testAdministrador = administradorService.createAdministrador(testAdministrador);
    }

    @Test
    @DisplayName("Prueba 1: Login exitoso de estudiante")
    void testLoginEstudianteExitoso() {
        // Verificar que el usuario existe
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEstudiante.getCorreo());
        assertNotNull(userDetails);
        assertEquals(testEstudiante.getCorreo(), userDetails.getUsername());

        // Verificar autenticación
        assertDoesNotThrow(() -> {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    testEstudiante.getCorreo(), 
                    "test123"
                )
            );
        });
    }

    @Test
    @DisplayName("Prueba 2: Login exitoso de administrador")
    void testLoginAdministradorExitoso() {
        // Verificar que el usuario existe
        UserDetails userDetails = userDetailsService.loadUserByUsername(testAdministrador.getCorreo());
        assertNotNull(userDetails);
        assertEquals(testAdministrador.getCorreo(), userDetails.getUsername());

        // Verificar autenticación
        assertDoesNotThrow(() -> {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    testAdministrador.getCorreo(), 
                    "admin123"
                )
            );
        });
    }

    @Test
    @DisplayName("Prueba 3: Login con credenciales incorrectas")
    void testLoginCredencialesIncorrectas() {
        // Verificar que la autenticación falla con contraseña incorrecta
        assertThrows(BadCredentialsException.class, () -> {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    testEstudiante.getCorreo(), 
                    "contraseña_incorrecta"
                )
            );
        });
    }

    @Test
    @DisplayName("Prueba 4: Login con usuario no existente")
    void testLoginUsuarioNoExiste() {
        // Verificar que la autenticación falla con usuario inexistente
        assertThrows(BadCredentialsException.class, () -> {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    "usuario.inexistente@test.com", 
                    "cualquier_password"
                )
            );
        });
    }

    @Test
    @DisplayName("Prueba 5: Verificar roles correctos")
    void testVerificarRoles() {
        // Verificar rol de estudiante
        UserDetails estudianteDetails = userDetailsService.loadUserByUsername(testEstudiante.getCorreo());
        assertTrue(estudianteDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ESTUDIANTE")));

        // Verificar rol de administrador
        UserDetails adminDetails = userDetailsService.loadUserByUsername(testAdministrador.getCorreo());
        assertTrue(adminDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRADOR")));
    }
}
