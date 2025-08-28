package com.gestaodeh.gestaodeh.repository;
import com.gestaodeh.gestaodeh.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorarioTrabalhoRepository extends JpaRepository<HorarioTrabalho, Long> { List<HorarioTrabalho> findByUserId(Long userId); }
