package com.f1news.backend.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.f1news.backend.model.Administrador;
import com.f1news.backend.model.Estudiante;
import com.f1news.backend.repository.AdministradorRepository;
import com.f1news.backend.repository.EstudianteRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario administrador si no existe
        if (!estudianteRepository.existsByCorreo("admin@universidad.edu")) {
            Estudiante admin = new Estudiante();
            admin.setNombreCompleto("Administrador Sistema");
            admin.setCorreo("admin@universidad.edu");
            admin.setTelefono("1234567890");
            admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
            admin.setCarrera("Administración");
            admin.setFotoUrl("https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face");
            admin.setEstado(Estudiante.EstadoEstudiante.ACTIVO);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(Estudiante.Rol.ADMINISTRADOR);
            
            estudianteRepository.save(admin);
            System.out.println("✅ Usuario administrador creado: admin@universidad.edu / admin123");
        }

        // Crear estudiante de ejemplo si no existe
        if (!estudianteRepository.existsByCorreo("juan.perez@estudiante.edu")) {
            Estudiante estudiante = new Estudiante();
            estudiante.setNombreCompleto("Juan Carlos Pérez");
            estudiante.setCorreo("juan.perez@estudiante.edu");
            estudiante.setTelefono("9876543210");
            estudiante.setFechaNacimiento(LocalDate.of(2000, 3, 15));
            estudiante.setCarrera("Ingeniería de Sistemas");
            estudiante.setEstado(Estudiante.EstadoEstudiante.ACTIVO);
            estudiante.setPassword(passwordEncoder.encode("student123"));
            estudiante.setRol(Estudiante.Rol.ESTUDIANTE);
            estudiante.setFotoUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face");
            
            estudianteRepository.save(estudiante);
            System.out.println("✅ Estudiante de ejemplo creado: juan.perez@estudiante.edu / student123");
        }

        // Crear algunos estudiantes adicionales
        crearEstudiantesEjemplo();
        
        // Crear administradores adicionales
        crearAdministradoresEjemplo();
    }

    private void crearEstudiantesEjemplo() {
        String[][] estudiantesData = {
            {"María González López", "maria.gonzalez@estudiante.edu", "8765432109", "1999-07-22", "Ingeniería Industrial", "https://images.unsplash.com/photo-1494790108755-2616b612b647?w=150&h=150&fit=crop&crop=face"},
            {"Carlos Alberto Ruiz", "carlos.ruiz@estudiante.edu", "7654321098", "2001-11-08", "Medicina", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face"},
            {"Ana Sofía Martínez", "ana.martinez@estudiante.edu", "6543210987", "2000-05-30", "Derecho", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face"},
            {"Pedro Pablo Jiménez", "pedro.jimenez@estudiante.edu", "5432109876", "1998-12-12", "Arquitectura", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face"}
        };

        for (String[] data : estudiantesData) {
            if (!estudianteRepository.existsByCorreo(data[1])) {
                Estudiante estudiante = new Estudiante();
                estudiante.setNombreCompleto(data[0]);
                estudiante.setCorreo(data[1]);
                estudiante.setTelefono(data[2]);
                estudiante.setFechaNacimiento(LocalDate.parse(data[3]));
                estudiante.setCarrera(data[4]);
                estudiante.setEstado(Estudiante.EstadoEstudiante.ACTIVO);
                estudiante.setPassword(passwordEncoder.encode("student123"));
                estudiante.setRol(Estudiante.Rol.ESTUDIANTE);
                estudiante.setFotoUrl(data[5]);
                
                estudianteRepository.save(estudiante);
                System.out.println("✅ Estudiante creado: " + data[1] + " / student123");
            }
        }
    }

    private void crearAdministradoresEjemplo() {
        // Crear administrador principal si no existe
        if (!administradorRepository.existsByCorreo("admin.principal@universidad.edu")) {
            Administrador admin = new Administrador();
            admin.setNombreCompleto("Director Académico");
            admin.setCorreo("admin.principal@universidad.edu");
            admin.setTelefono("1234567890");
            admin.setFechaNacimiento(LocalDate.of(1985, 6, 15));
            admin.setFotoUrl("https://images.unsplash.com/photo-1560250097-0b93528c311a?w=150&h=150&fit=crop&crop=face");
            admin.setEstado(Administrador.EstadoAdministrador.ACTIVO);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(Administrador.Rol.ADMINISTRADOR);
            
            administradorRepository.save(admin);
            System.out.println("✅ Administrador principal creado: admin.principal@universidad.edu / admin123");
        }

        // Crear algunos administradores adicionales
        String[][] adminsData = {
            {"Coordinador de Sistemas", "coord.sistemas@universidad.edu", "9876543210", "1980-03-20", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face"},
            {"Secretaria Académica", "secretaria@universidad.edu", "8765432109", "1982-09-12", "https://images.unsplash.com/photo-1494790108755-2616b612b647?w=150&h=150&fit=crop&crop=face"}
        };

        for (String[] data : adminsData) {
            if (!administradorRepository.existsByCorreo(data[1])) {
                Administrador admin = new Administrador();
                admin.setNombreCompleto(data[0]);
                admin.setCorreo(data[1]);
                admin.setTelefono(data[2]);
                admin.setFechaNacimiento(LocalDate.parse(data[3]));
                admin.setEstado(Administrador.EstadoAdministrador.ACTIVO);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol(Administrador.Rol.ADMINISTRADOR);
                admin.setFotoUrl(data[4]);
                
                administradorRepository.save(admin);
                System.out.println("✅ Administrador creado: " + data[1] + " / admin123");
            }
        }
    }
}
