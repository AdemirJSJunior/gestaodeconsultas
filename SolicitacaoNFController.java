// Arquivo: src/main/java/com/gestaodeh/gestaodeh/controller/SolicitacaoNFController.java
package com.gestaodeh.gestaodeh.controller;

import com.gestaodeh.gestaodeh.model.Agendamento;
import com.gestaodeh.gestaodeh.model.AgendamentosSolicitacaoNf;
import com.gestaodeh.gestaodeh.model.SolicitacaoNF;
import com.gestaodeh.gestaodeh.payload.request.SolicitacaoNFRequest;
import com.gestaodeh.gestaodeh.repository.AgendamentoRepository;
import com.gestaodeh.gestaodeh.repository.AgendamentosSolicitacaoNfRepository;
import com.gestaodeh.gestaodeh.repository.SolicitacaoNFRepository;
import com.gestaodeh.gestaodeh.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes-nf")
@CrossOrigin(origins = "*")
public class SolicitacaoNFController {
    @Autowired private SolicitacaoNFRepository solicitacaoNFRepository;
    @Autowired private AgendamentoRepository agendamentoRepository;
    @Autowired private AgendamentosSolicitacaoNfRepository agendamentosSolicitacaoNfRepository;

    private Long getUserIdLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
    
    @GetMapping
    public List<SolicitacaoNF> listarSolicitacoes() {
        return solicitacaoNFRepository.findByUserId(getUserIdLogado());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoNF> buscarPorId(@PathVariable Long id) {
        return solicitacaoNFRepository.findById(id)
            .filter(s -> s.getUserId().equals(getUserIdLogado()))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SolicitacaoNF criarSolicitacao(@RequestBody SolicitacaoNFRequest request) {
        Long userId = getUserIdLogado();
        SolicitacaoNF solicitacao = new SolicitacaoNF();
        solicitacao.setUserId(userId);
        solicitacao.setPacienteId(request.getPacienteId());
        solicitacao.setPacienteNomeNf(request.getPacienteNomeNf());
        solicitacao.setPacienteCpfNf(request.getPacienteCpfNf());
        solicitacao.setPacienteEnderecoNf(request.getPacienteEnderecoNf());
        solicitacao.setStatus(SolicitacaoNF.StatusNF.Pendente);
        BigDecimal valorTotal = agendamentoRepository.findAllById(request.getAgendamentoIds()).stream().map(Agendamento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        solicitacao.setValorTotal(valorTotal);
        SolicitacaoNF solicitacaoSalva = solicitacaoNFRepository.save(solicitacao);
        request.getAgendamentoIds().forEach(agendamentoId -> {
            AgendamentosSolicitacaoNf ligacao = new AgendamentosSolicitacaoNf(solicitacaoSalva.getId(), agendamentoId);
            agendamentosSolicitacaoNfRepository.save(ligacao);
        });
        return solicitacaoSalva;
    }
}