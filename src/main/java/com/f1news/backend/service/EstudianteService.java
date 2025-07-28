package com.f1news.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.f1news.backend.model.Estudiante;
import com.f1news.backend.repository.EstudianteRepository;

@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Estudiante> getAllEstudiantes() {
        return estudianteRepository.findAll();
    }

    public Optional<Estudiante> getEstudianteById(Long id) {
        return estudianteRepository.findById(id);
    }

    public Optional<Estudiante> getEstudianteByCorreo(String correo) {
        return estudianteRepository.findByCorreo(correo);
    }

    public Estudiante createEstudiante(Estudiante estudiante) {
        System.out.println("=== DEBUG: EstudianteService.createEstudiante ===");
        System.out.println("Verificando si existe correo: " + estudiante.getCorreo());
        
        if (estudianteRepository.existsByCorreo(estudiante.getCorreo())) {
            System.err.println("❌ Error: Ya existe un estudiante con ese correo");
            throw new RuntimeException("Ya existe un estudiante con ese correo");
        }
        
        // Asegurar valores por defecto válidos
        if (estudiante.getEstado() == null) {
            System.out.println("Asignando estado por defecto: ACTIVO");
            estudiante.setEstado(Estudiante.EstadoEstudiante.ACTIVO);
        }
        if (estudiante.getRol() == null) {
            System.out.println("Asignando rol por defecto: ESTUDIANTE");
            estudiante.setRol(Estudiante.Rol.ESTUDIANTE);
        }
        
        // Verificar que la contraseña no sea null o vacía
        String password = estudiante.getPassword();
        System.out.println("Password recibido: " + (password != null ? "NO NULL (length: " + password.length() + ")" : "NULL"));
        
        if (password == null || password.trim().isEmpty()) {
            System.err.println("❌ Error: La contraseña es requerida");
            throw new RuntimeException("La contraseña es requerida para crear un estudiante");
        }
        
        System.out.println("Encriptando contraseña...");
        // Encriptar la contraseña
        estudiante.setPassword(passwordEncoder.encode(password.trim()));
        
        System.out.println("Guardando estudiante en la base de datos...");
        Estudiante resultado = estudianteRepository.save(estudiante);
        System.out.println("✅ Estudiante guardado exitosamente con ID: " + resultado.getId());
        
        return resultado;
    }

    public Estudiante updateEstudiante(Long id, Estudiante estudianteDetails) {
        return estudianteRepository.findById(id)
                .map(estudiante -> {
                    estudiante.setNombreCompleto(estudianteDetails.getNombreCompleto());
                    estudiante.setTelefono(estudianteDetails.getTelefono());
                    estudiante.setFechaNacimiento(estudianteDetails.getFechaNacimiento());
                    estudiante.setCarrera(estudianteDetails.getCarrera());
                    estudiante.setEstado(estudianteDetails.getEstado());
                    
                    // Actualizar foto URL si se proporciona
                    if (estudianteDetails.getFotoUrl() != null) {
                        estudiante.setFotoUrl(estudianteDetails.getFotoUrl());
                    }
                    
                    // Solo actualizar el correo si es diferente y no existe
                    if (!estudiante.getCorreo().equals(estudianteDetails.getCorreo())) {
                        if (estudianteRepository.existsByCorreo(estudianteDetails.getCorreo())) {
                            throw new RuntimeException("Ya existe un estudiante con ese correo");
                        }
                        estudiante.setCorreo(estudianteDetails.getCorreo());
                    }
                    
                    // Solo actualizar la contraseña si se proporciona una nueva
                    if (estudianteDetails.getPassword() != null && !estudianteDetails.getPassword().isEmpty()) {
                        estudiante.setPassword(passwordEncoder.encode(estudianteDetails.getPassword()));
                    }
                    
                    return estudianteRepository.save(estudiante);
                })
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));
    }

    public void deleteEstudiante(Long id) {
        if (!estudianteRepository.existsById(id)) {
            throw new RuntimeException("Estudiante no encontrado con id: " + id);
        }
        estudianteRepository.deleteById(id);
    }

    public List<Estudiante> getEstudiantesByEstado(Estudiante.EstadoEstudiante estado) {
        return estudianteRepository.findByEstado(estado);
    }

    public List<Estudiante> getEstudiantesByCarrera(String carrera) {
        return estudianteRepository.findByCarrera(carrera);
    }

    public List<Estudiante> searchEstudiantesByNombre(String nombre) {
        return estudianteRepository.findByNombreCompletoContaining(nombre);
    }

    public long countEstudiantesByEstado(Estudiante.EstadoEstudiante estado) {
        return estudianteRepository.countByEstado(estado);
    }
}
