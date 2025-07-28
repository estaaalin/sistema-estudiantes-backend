package com.f1news.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.f1news.backend.config.JwtUtil;
import com.f1news.backend.model.Estudiante;
import com.f1news.backend.service.EstudianteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint para que ADMINISTRADOR obtenga todos los estudiantes
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Estudiante>> getAllEstudiantes() {
        try {
            List<Estudiante> estudiantes = estudianteService.getAllEstudiantes();
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoint para que ADMINISTRADOR cree un nuevo estudiante
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> createEstudiante(@Valid @RequestBody Estudiante estudiante) {
        try {
            System.out.println("=== DEBUG: Creando estudiante ===");
            System.out.println("Nombre: " + estudiante.getNombreCompleto());
            System.out.println("Correo: " + estudiante.getCorreo());
            System.out.println("Teléfono: " + estudiante.getTelefono());
            System.out.println("Fecha nacimiento: " + estudiante.getFechaNacimiento());
            System.out.println("Carrera: " + estudiante.getCarrera());
            System.out.println("Estado: " + estudiante.getEstado());
            System.out.println("Rol: " + estudiante.getRol());
            System.out.println("Password presente: " + (estudiante.getPassword() != null && !estudiante.getPassword().isEmpty()));
            System.out.println("=================================");
            
            Estudiante nuevoEstudiante = estudianteService.createEstudiante(estudiante);
            return ResponseEntity.ok(nuevoEstudiante);
        } catch (RuntimeException e) {
            System.err.println("❌ RuntimeException al crear estudiante: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.err.println("❌ Exception al crear estudiante: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Endpoint para obtener un estudiante por ID
    // ADMINISTRADOR puede ver cualquier estudiante
    // ESTUDIANTE solo puede ver su propio perfil
    @GetMapping("/{id}")
    public ResponseEntity<?> getEstudianteById(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String userRole = jwtUtil.extractRole(token);
            Long userId = jwtUtil.extractUserId(token);

            // Si es estudiante, solo puede ver su propio perfil
            if ("ESTUDIANTE".equals(userRole) && !userId.equals(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permisos para ver esta información");
                return ResponseEntity.status(403).body(error);
            }

            Optional<Estudiante> estudiante = estudianteService.getEstudianteById(id);
            if (estudiante.isPresent()) {
                return ResponseEntity.ok(estudiante.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Estudiante no encontrado");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Endpoint para actualizar un estudiante
    // ADMINISTRADOR puede editar cualquier estudiante
    // ESTUDIANTE solo puede editar su propia información
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEstudiante(@PathVariable Long id, 
                                            @Valid @RequestBody Estudiante estudianteDetails,
                                            HttpServletRequest request) {
        try {
            System.out.println("=== ACTUALIZANDO ESTUDIANTE ID: " + id + " ===");
            System.out.println("Nombre: " + estudianteDetails.getNombreCompleto());
            System.out.println("Correo: " + estudianteDetails.getCorreo());
            System.out.println("FotoUrl presente: " + (estudianteDetails.getFotoUrl() != null));
            if (estudianteDetails.getFotoUrl() != null) {
                System.out.println("FotoUrl length: " + estudianteDetails.getFotoUrl().length());
                System.out.println("FotoUrl preview: " + estudianteDetails.getFotoUrl().substring(0, Math.min(100, estudianteDetails.getFotoUrl().length())) + "...");
            }
            System.out.println("===========================================");
            
            String token = extractTokenFromRequest(request);
            String userRole = jwtUtil.extractRole(token);
            Long userId = jwtUtil.extractUserId(token);

            // Si es estudiante, solo puede editar su propio perfil
            if ("ESTUDIANTE".equals(userRole) && !userId.equals(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permisos para editar esta información");
                return ResponseEntity.status(403).body(error);
            }

            Estudiante estudianteActualizado = estudianteService.updateEstudiante(id, estudianteDetails);
            System.out.println("✅ Estudiante actualizado exitosamente con ID: " + estudianteActualizado.getId());
            return ResponseEntity.ok(estudianteActualizado);
        } catch (RuntimeException e) {
            System.err.println("❌ RuntimeException al actualizar estudiante: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.err.println("❌ Exception al actualizar estudiante: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Endpoint para eliminar un estudiante - Solo ADMINISTRADOR
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> deleteEstudiante(@PathVariable Long id) {
        try {
            estudianteService.deleteEstudiante(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Estudiante eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Endpoints adicionales para ADMINISTRADOR
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Estudiante>> getEstudiantesByEstado(@PathVariable String estado) {
        try {
            Estudiante.EstadoEstudiante estadoEnum = Estudiante.EstadoEstudiante.valueOf(estado.toUpperCase());
            List<Estudiante> estudiantes = estudianteService.getEstudiantesByEstado(estadoEnum);
            return ResponseEntity.ok(estudiantes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/carrera/{carrera}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Estudiante>> getEstudiantesByCarrera(@PathVariable String carrera) {
        try {
            List<Estudiante> estudiantes = estudianteService.getEstudiantesByCarrera(carrera);
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Estudiante>> searchEstudiantes(@RequestParam String nombre) {
        try {
            List<Estudiante> estudiantes = estudianteService.searchEstudiantesByNombre(nombre);
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Método auxiliar para extraer el token del request
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
