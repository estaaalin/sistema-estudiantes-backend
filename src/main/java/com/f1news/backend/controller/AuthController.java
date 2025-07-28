package com.f1news.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.f1news.backend.config.JwtUtil;
import com.f1news.backend.model.Administrador;
import com.f1news.backend.model.Estudiante;
import com.f1news.backend.service.CustomUserDetailsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getCorreo(),
                            loginRequest.getPassword()
                    )
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getCorreo());
            final Object user = userDetailsService.getUserByCorreo(loginRequest.getCorreo());
            
            Map<String, Object> response = new HashMap<>();
            
            if (user instanceof Estudiante) {
                Estudiante estudiante = (Estudiante) user;
                final String jwt = jwtUtil.generateToken(
                        userDetails, 
                        estudiante.getId(), 
                        estudiante.getRol().name()
                );
                
                response.put("token", jwt);
                response.put("type", "Bearer");
                response.put("id", estudiante.getId());
                response.put("correo", estudiante.getCorreo());
                response.put("nombreCompleto", estudiante.getNombreCompleto());
                response.put("rol", estudiante.getRol().name());
                
            } else if (user instanceof Administrador) {
                Administrador administrador = (Administrador) user;
                final String jwt = jwtUtil.generateToken(
                        userDetails, 
                        administrador.getId(), 
                        administrador.getRol().name()
                );
                
                response.put("token", jwt);
                response.put("type", "Bearer");
                response.put("id", administrador.getId());
                response.put("correo", administrador.getCorreo());
                response.put("nombreCompleto", administrador.getNombreCompleto());
                response.put("rol", administrador.getRol().name());
            }

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error en el servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    public static class LoginRequest {
        private String correo;
        private String password;

        // Constructores
        public LoginRequest() {}

        public LoginRequest(String correo, String password) {
            this.correo = correo;
            this.password = password;
        }

        // Getters y Setters
        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
