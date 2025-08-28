package com.gestaodeh.gestaodeh.repository;
import com.gestaodeh.gestaodeh.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitacaoNFRepository extends JpaRepository<SolicitacaoNF, Long> { List<SolicitacaoNF> findByUserId(Long userId); }
