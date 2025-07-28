package com.f1news.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.f1news.backend.model.Administrador;
import com.f1news.backend.repository.AdministradorRepository;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Administrador> getAllAdministradores() {
        return administradorRepository.findAll();
    }

    public Optional<Administrador> getAdministradorById(Long id) {
        return administradorRepository.findById(id);
    }

    public Optional<Administrador> getAdministradorByCorreo(String correo) {
        return administradorRepository.findByCorreo(correo);
    }

    public Administrador createAdministrador(Administrador administrador) {
        System.out.println("=== CREANDO ADMINISTRADOR ===");
        System.out.println("Nombre: " + administrador.getNombreCompleto());
        System.out.println("Correo: " + administrador.getCorreo());
        System.out.println("Teléfono: " + administrador.getTelefono());
        System.out.println("Fecha nacimiento: " + administrador.getFechaNacimiento());
        System.out.println("Estado: " + administrador.getEstado());
        System.out.println("Rol: " + administrador.getRol());
        System.out.println("Password presente: " + (administrador.getPassword() != null && !administrador.getPassword().isEmpty()));
        System.out.println("FotoUrl presente: " + (administrador.getFotoUrl() != null));
        System.out.println("=================================");

        // Verificar si ya existe un administrador con ese correo
        if (administradorRepository.existsByCorreo(administrador.getCorreo())) {
            throw new RuntimeException("Ya existe un administrador con ese correo");
        }

        // Validar que la contraseña esté presente para la creación
        if (administrador.getPassword() == null || administrador.getPassword().trim().isEmpty()) {
            throw new RuntimeException("La contraseña es requerida para crear un nuevo administrador");
        }

        // Encriptar la contraseña
        administrador.setPassword(passwordEncoder.encode(administrador.getPassword()));

        // Establecer valores por defecto
        if (administrador.getEstado() == null) {
            administrador.setEstado(Administrador.EstadoAdministrador.ACTIVO);
        }
        if (administrador.getRol() == null) {
            administrador.setRol(Administrador.Rol.ADMINISTRADOR);
        }

        Administrador resultado = administradorRepository.save(administrador);
        System.out.println("✅ Administrador creado exitosamente con ID: " + resultado.getId());
        return resultado;
    }

    public Administrador updateAdministrador(Long id, Administrador administradorDetails) {
        return administradorRepository.findById(id)
                .map(administrador -> {
                    System.out.println("=== ACTUALIZANDO ADMINISTRADOR ID: " + id + " ===");
                    System.out.println("Nombre: " + administradorDetails.getNombreCompleto());
                    System.out.println("Correo: " + administradorDetails.getCorreo());
                    System.out.println("Password presente en request: " + (administradorDetails.getPassword() != null));
                    if (administradorDetails.getPassword() != null) {
                        System.out.println("Password vacío: " + administradorDetails.getPassword().isEmpty());
                    }
                    System.out.println("FotoUrl presente: " + (administradorDetails.getFotoUrl() != null));
                    if (administradorDetails.getFotoUrl() != null) {
                        System.out.println("FotoUrl length: " + administradorDetails.getFotoUrl().length());
                        System.out.println("FotoUrl preview: " + administradorDetails.getFotoUrl().substring(0, Math.min(100, administradorDetails.getFotoUrl().length())) + "...");
                    }
                    System.out.println("===========================================");

                    administrador.setNombreCompleto(administradorDetails.getNombreCompleto());
                    administrador.setTelefono(administradorDetails.getTelefono());
                    administrador.setFechaNacimiento(administradorDetails.getFechaNacimiento());
                    administrador.setEstado(administradorDetails.getEstado());
                    administrador.setRol(administradorDetails.getRol());
                    
                    // Actualizar foto URL si se proporciona
                    if (administradorDetails.getFotoUrl() != null) {
                        administrador.setFotoUrl(administradorDetails.getFotoUrl());
                    }
                    
                    // Solo actualizar el correo si es diferente y no existe
                    if (!administrador.getCorreo().equals(administradorDetails.getCorreo())) {
                        if (administradorRepository.existsByCorreo(administradorDetails.getCorreo())) {
                            throw new RuntimeException("Ya existe un administrador con ese correo");
                        }
                        administrador.setCorreo(administradorDetails.getCorreo());
                    }
                    
                    // Solo actualizar la contraseña si se proporciona una nueva
                    if (administradorDetails.getPassword() != null && !administradorDetails.getPassword().isEmpty()) {
                        administrador.setPassword(passwordEncoder.encode(administradorDetails.getPassword()));
                        System.out.println("✅ Contraseña actualizada");
                    } else {
                        System.out.println("ℹ️  No se actualiza la contraseña (no proporcionada o vacía)");
                    }
                    
                    Administrador resultado = administradorRepository.save(administrador);
                    System.out.println("✅ Administrador actualizado exitosamente con ID: " + resultado.getId());
                    return resultado;
                })
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con id: " + id));
    }

    public void deleteAdministrador(Long id) {
        if (!administradorRepository.existsById(id)) {
            throw new RuntimeException("Administrador no encontrado con id: " + id);
        }
        administradorRepository.deleteById(id);
    }

    public List<Administrador> getAdministradoresByEstado(Administrador.EstadoAdministrador estado) {
        return administradorRepository.findByEstado(estado);
    }

    public List<Administrador> getAdministradoresByRol(Administrador.Rol rol) {
        return administradorRepository.findByRol(rol);
    }

    public List<Administrador> searchAdministradores(String nombre) {
        return administradorRepository.findByNombreCompletoContainingIgnoreCase(nombre);
    }
}
