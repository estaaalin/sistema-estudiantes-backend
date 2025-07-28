package com.f1news.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.f1news.backend.model.Estudiante;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    
    Optional<Estudiante> findByCorreo(String correo);
    
    boolean existsByCorreo(String correo);
    
    List<Estudiante> findByEstado(Estudiante.EstadoEstudiante estado);
    
    List<Estudiante> findByCarrera(String carrera);
    
    @Query("SELECT e FROM Estudiante e WHERE e.nombreCompleto LIKE %:nombre%")
    List<Estudiante> findByNombreCompletoContaining(@Param("nombre") String nombre);
    
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.estado = :estado")
    long countByEstado(@Param("estado") Estudiante.EstadoEstudiante estado);
    
    List<Estudiante> findByRol(Estudiante.Rol rol);
}
