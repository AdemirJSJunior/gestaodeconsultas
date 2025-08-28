// Arquivo: src/main/java/com/gestaodeh/gestaodeh/controller/AgendamentoController.java
package com.gestaodeh.gestaodeh.controller;

import com.gestaodeh.gestaodeh.model.Agendamento;
import com.gestaodeh.gestaodeh.model.AgendamentosSolicitacaoNf;
import com.gestaodeh.gestaodeh.repository.AgendamentoRepository;
import com.gestaodeh.gestaodeh.repository.AgendamentosSolicitacaoNfRepository;
import com.gestaodeh.gestaodeh.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {
    @Autowired private AgendamentoRepository agendamentoRepository;
    @Autowired private AgendamentosSolicitacaoNfRepository agendamentosSolicitacaoNfRepository;

    private Long getUserIdLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public List<Agendamento> listarAgendamentos() {
        return agendamentoRepository.findByUserId(getUserIdLogado());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        return agendamentoRepository.findById(id)
            .filter(a -> a.getUserId().equals(getUserIdLogado()))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Agendamento criarAgendamento(@RequestBody Agendamento agendamento) {
        agendamento.setUserId(getUserIdLogado());
        return agendamentoRepository.save(agendamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> atualizarAgendamento(@PathVariable Long id, @RequestBody Agendamento detalhes) {
        return (ResponseEntity<Agendamento>) agendamentoRepository.findById(id)
            .map(agendamento -> {
                if (!agendamento.getUserId().equals(getUserIdLogado())) {
                    return ResponseEntity.status(403).build();
                }
                agendamento.setPacienteId(detalhes.getPacienteId());
                agendamento.setDataHora(detalhes.getDataHora());
                agendamento.setValor(detalhes.getValor());
                agendamento.setStatus(detalhes.getStatus());
                agendamento.setObservacoes(detalhes.getObservacoes());
                return ResponseEntity.ok(agendamentoRepository.save(agendamento));
            }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarAgendamento(@PathVariable Long id) {
        return agendamentoRepository.findById(id).map(agendamento -> {
            if (!agendamento.getUserId().equals(getUserIdLogado())) {
                return ResponseEntity.status(403).build();
            }
            agendamentoRepository.delete(agendamento);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponiveis-para-nf/{pacienteId}")
    public ResponseEntity<List<Agendamento>> getAgendamentosDisponiveisParaNf(@PathVariable Long pacienteId) {
        Long userId = getUserIdLogado();
        List<Long> idsJaUtilizados = agendamentosSolicitacaoNfRepository.findAll().stream().map(AgendamentosSolicitacaoNf::getAgendamentoId).toList();
        List<Agendamento> agendamentosDisponiveis = agendamentoRepository.findByUserId(userId).stream()
            .filter(a -> a.getPacienteId().equals(pacienteId) && a.getStatus() == Agendamento.StatusAgendamento.Pago && !idsJaUtilizados.contains(a.getId()))
            .toList();
        return ResponseEntity.ok(agendamentosDisponiveis);
    }
}