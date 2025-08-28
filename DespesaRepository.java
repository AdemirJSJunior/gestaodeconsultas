package com.gestaodeh.gestaodeh.repository;
import com.gestaodeh.gestaodeh.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> { List<Despesa> findByUserId(Long userId); }
