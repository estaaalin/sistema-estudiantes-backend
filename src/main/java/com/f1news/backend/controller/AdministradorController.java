package com.f1news.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.f1news.backend.model.Administrador;
import com.f1news.backend.service.AdministradorService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/administradores")
@CrossOrigin(origins = "http://localhost:3001")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private JwtUtil jwtUtil;

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Endpoint para obtener todos los administradores - Solo ADMINISTRADOR
    @GetMapping
    public ResponseEntity<?> getAllAdministradores(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String userRole = jwtUtil.extractRole(token);

            if (!"ADMINISTRADOR".equals(userRole)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permisos para acceder a esta información");
                return ResponseEntity.status(403).body(error);
            }

            List<Administrador> administradores = administradorService.getAllAdministradores();
            return ResponseEntity.ok(administradores);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Endpoint para obtener un administrador por ID - Solo ADMINISTRADOR
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdministradorById(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String userRole = jwtUtil.extractRole(token);
            Long userId = jwtUtil.extractUserId(token);

            // Un administrador puede ver su propio perfil o cualquier perfil si es admin
            if (!"ADMINISTRADOR".equals(userRole) && !userId.equals(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permisos para acceder a esta información");
                return ResponseEntity.status(403).body(error);
            }

            return administradorService.getAdministradorById(id)
                    .map(administrador -> ResponseEntity.ok(administrador))
                    .orElseGet(() -> {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Administrador no encontrado");
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Endpoint para crear un nuevo administrador - Solo ADMINISTRADOR
    @PostMapping
    public ResponseEntity<?> createAdministrador(@Valid @RequestBody Administrador administrador, 
                                               HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String userRole = jwtUtil.extractRole(token);

            if (!"ADMINISTRADOR".equals(userRole)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permisos para crear administradores");
                return ResponseEntity.status(403).body(error);
            }

            System.out.println("=== RECIBIENDO PETICIÓN CREAR ADMINISTRADOR ===");
            System.out.println("Nombre: " + administrador.getNombreCompleto());
            System.out.println("Correo: " + administrador.getCorreo());
            System.out.println("Teléfono: " + administrador.getTelefono());
            System.out.println("Fecha nacimiento: " + administrador.getFechaNacimiento());
            System.out.println("Estado: " + administrador.getEstado());
            System.out.println("Rol: " + administrador.getRol());
            System.out.println("Password presente: " + (administrador.getPassword() != null && !administrador.getPassword().isEmpty()));
            System.out.println("=================================");
            
            Administrador nuevoAdministrador = administradorService.createAdministrador(administrador);
            return ResponseEntity.ok(nuevoAdministrador);
        } catch (RuntimeException e) {
            System.err.println("❌ RuntimeException al crear administrador: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.err.println("❌ Exception al crear administrador: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Endpoint para actualizar un administrador - Solo ADMINISTRADOR
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdministrador(@PathVariable Long id, 
                                               @Valid @RequestBody Administrador administradorDetails,
                                               HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String userRole = jwtUtil.extractRole(token);
            Long userId = jwtUtil.extractUserId(token);

            // Un administrador puede editar su propio perfil o cualquier perfil si es admin
            if (!"ADMINISTRADOR".equals(userRole) && !userId.equals(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permisos para editar esta información");
                return ResponseEntity.status(403).body(error);
            }

            Administrador administradorActualizado = administradorService.updateAdministrador(id, administradorDetails);
            return ResponseEntity.ok(administradorActualizado);
        } catch (RuntimeException e) {
            System.err.println("❌ RuntimeException al actualizar administrador: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.err.println("❌ Exception al actualizar administrador: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Endpoint para eliminar un administrador - Solo ADMINISTRADOR
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdministrador(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String userRole = jwtUtil.extractRole(token);

            if (!"ADMINISTRADOR".equals(userRole)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permisos para eliminar administradores");
                return ResponseEntity.status(403).body(error);
            }

            administradorService.deleteAdministrador(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Administrador eliminado correctamente");
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

    // Endpoint para buscar administradores por nombre - Solo ADMINISTRADOR
    @GetMapping("/buscar")
    public ResponseEntity<?> searchAdministradores(@RequestParam String nombre, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String userRole = jwtUtil.extractRole(token);

            if (!"ADMINISTRADOR".equals(userRole)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permisos para buscar administradores");
                return ResponseEntity.status(403).body(error);
            }

            List<Administrador> administradores = administradorService.searchAdministradores(nombre);
            return ResponseEntity.ok(administradores);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Endpoint para filtrar por estado - Solo ADMINISTRADOR
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getAdministradoresByEstado(@PathVariable String estado, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String userRole = jwtUtil.extractRole(token);

            if (!"ADMINISTRADOR".equals(userRole)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No tienes permisos para acceder a esta información");
                return ResponseEntity.status(403).body(error);
            }

            Administrador.EstadoAdministrador estadoEnum = Administrador.EstadoAdministrador.valueOf(estado.toUpperCase());
            List<Administrador> administradores = administradorService.getAdministradoresByEstado(estadoEnum);
            return ResponseEntity.ok(administradores);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Estado inválido");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
