package com.gestaodeh.gestaodeh.repository;
import com.gestaodeh.gestaodeh.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> { List<Agendamento> findByUserId(Long userId); }
