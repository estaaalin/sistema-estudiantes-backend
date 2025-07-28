package com.f1news.backend;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.f1news.backend.model.Estudiante;
import com.f1news.backend.repository.EstudianteRepository;
import com.f1news.backend.service.EstudianteService;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EstudianteService estudianteService;

    @Test
    void testGetAllEstudiantes() {
        // Arrange
        Estudiante estudiante1 = new Estudiante("Juan Pérez", "juan@test.com", "123456789", 
                                               LocalDate.of(2000, 1, 1), "Ingeniería", "password");
        Estudiante estudiante2 = new Estudiante("María García", "maria@test.com", "987654321", 
                                               LocalDate.of(1999, 5, 15), "Medicina", "password");
        List<Estudiante> estudiantes = Arrays.asList(estudiante1, estudiante2);

        when(estudianteRepository.findAll()).thenReturn(estudiantes);

        // Act
        List<Estudiante> result = estudianteService.getAllEstudiantes();

        // Assert
        assertEquals(2, result.size());
        verify(estudianteRepository, times(1)).findAll();
    }

    @Test
    void testCreateEstudiante() {
        // Arrange
        Estudiante estudiante = new Estudiante("Test User", "test@test.com", "123456789", 
                                             LocalDate.of(2000, 1, 1), "Ingeniería", "password");
        
        when(estudianteRepository.existsByCorreo(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encrypted_password");
        when(estudianteRepository.save(any(Estudiante.class))).thenReturn(estudiante);

        // Act
        Estudiante result = estudianteService.createEstudiante(estudiante);

        // Assert
        assertNotNull(result);
        verify(estudianteRepository, times(1)).existsByCorreo(estudiante.getCorreo());
        verify(passwordEncoder, times(1)).encode("password");
        verify(estudianteRepository, times(1)).save(estudiante);
    }

    @Test
    void testCreateEstudianteWithDuplicateEmail() {
        // Arrange
        Estudiante estudiante = new Estudiante("Test User", "test@test.com", "123456789", 
                                             LocalDate.of(2000, 1, 1), "Ingeniería", "password");
        
        when(estudianteRepository.existsByCorreo(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            estudianteService.createEstudiante(estudiante);
        });

        assertEquals("Ya existe un estudiante con ese correo", exception.getMessage());
        verify(estudianteRepository, times(1)).existsByCorreo(estudiante.getCorreo());
        verify(estudianteRepository, never()).save(any(Estudiante.class));
    }

    @Test
    void testGetEstudianteById() {
        // Arrange
        Long id = 1L;
        Estudiante estudiante = new Estudiante("Test User", "test@test.com", "123456789", 
                                             LocalDate.of(2000, 1, 1), "Ingeniería", "password");
        estudiante.setId(id);

        when(estudianteRepository.findById(id)).thenReturn(Optional.of(estudiante));

        // Act
        Optional<Estudiante> result = estudianteService.getEstudianteById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(estudianteRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteEstudiante() {
        // Arrange
        Long id = 1L;
        when(estudianteRepository.existsById(id)).thenReturn(true);

        // Act
        estudianteService.deleteEstudiante(id);

        // Assert
        verify(estudianteRepository, times(1)).existsById(id);
        verify(estudianteRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteNonExistentEstudiante() {
        // Arrange
        Long id = 1L;
        when(estudianteRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            estudianteService.deleteEstudiante(id);
        });

        assertEquals("Estudiante no encontrado con id: " + id, exception.getMessage());
        verify(estudianteRepository, times(1)).existsById(id);
        verify(estudianteRepository, never()).deleteById(any());
    }
}
