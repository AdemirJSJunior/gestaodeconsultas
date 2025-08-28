package com.gestaodeh.gestaodeh.repository;
import com.gestaodeh.gestaodeh.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> { List<Paciente> findByUserId(Long userId); }
