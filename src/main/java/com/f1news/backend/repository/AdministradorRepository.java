package com.f1news.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.f1news.backend.model.Administrador;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    
    Optional<Administrador> findByCorreo(String correo);
    
    boolean existsByCorreo(String correo);
    
    List<Administrador> findByEstado(Administrador.EstadoAdministrador estado);
    
    List<Administrador> findByRol(Administrador.Rol rol);
    
    List<Administrador> findByNombreCompletoContainingIgnoreCase(String nombre);
}
