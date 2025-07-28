package com.f1news.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.f1news.backend.model.Administrador;
import com.f1news.backend.model.Estudiante;
import com.f1news.backend.repository.AdministradorRepository;
import com.f1news.backend.repository.EstudianteRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // Primero buscar en estudiantes
        Estudiante estudiante = estudianteRepository.findByCorreo(correo).orElse(null);
        if (estudiante != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + estudiante.getRol().name()));

            return new User(
                    estudiante.getCorreo(),
                    estudiante.getPassword(),
                    estudiante.getEstado() == Estudiante.EstadoEstudiante.ACTIVO,
                    true,
                    true,
                    true,
                    authorities
            );
        }

        // Si no se encuentra en estudiantes, buscar en administradores
        Administrador administrador = administradorRepository.findByCorreo(correo).orElse(null);
        if (administrador != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + administrador.getRol().name()));

            return new User(
                    administrador.getCorreo(),
                    administrador.getPassword(),
                    administrador.getEstado() == Administrador.EstadoAdministrador.ACTIVO,
                    true,
                    true,
                    true,
                    authorities
            );
        }

        // Si no se encuentra en ninguna tabla
        throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
    }

    public Estudiante getEstudianteByCorreo(String correo) {
        return estudianteRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));
    }

    public Object getUserByCorreo(String correo) {
        // Buscar primero en estudiantes
        Estudiante estudiante = estudianteRepository.findByCorreo(correo).orElse(null);
        if (estudiante != null) {
            return estudiante;
        }
        
        // Si no se encuentra, buscar en administradores
        Administrador administrador = administradorRepository.findByCorreo(correo).orElse(null);
        if (administrador != null) {
            return administrador;
        }
        
        throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
    }
}
