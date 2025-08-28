// Arquivo: src/main/java/com/gestaodeh/gestaodeh/controller/FinanceiroController.java
package com.gestaodeh.gestaodeh.controller;

import com.gestaodeh.gestaodeh.model.Agendamento;
import com.gestaodeh.gestaodeh.model.Paciente;
import com.gestaodeh.gestaodeh.payload.response.FinanceiroAVencerDTO;
import com.gestaodeh.gestaodeh.payload.response.FinanceiroVencidoDTO;
import com.gestaodeh.gestaodeh.payload.response.RelatorioFinanceiroDTO;
import com.gestaodeh.gestaodeh.repository.AgendamentoRepository;
import com.gestaodeh.gestaodeh.repository.PacienteRepository;
import com.gestaodeh.gestaodeh.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/financeiro")
@CrossOrigin(origins = "*")
public class FinanceiroController {
    @Autowired private AgendamentoRepository agendamentoRepository;
    @Autowired private PacienteRepository pacienteRepository;

    private Long getUserIdLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping("/a-receber")
    public ResponseEntity<RelatorioFinanceiroDTO> getRelatorioAReceber() {
        Long userId = getUserIdLogado();
        LocalDateTime agora = LocalDateTime.now();
        List<Agendamento> agendamentosEmAberto = agendamentoRepository.findByUserId(userId).stream()
                .filter(a -> a.getStatus() == Agendamento.StatusAgendamento.Em_Aberto).toList();
        Map<Long, String> pacientesMap = pacienteRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(Paciente::getId, Paciente::getNomeCompleto));
        List<Agendamento> vencidos = agendamentosEmAberto.stream().filter(a -> a.getDataHora().isBefore(agora)).toList();
        List<Agendamento> aVencer = agendamentosEmAberto.stream().filter(a -> !a.getDataHora().isBefore(agora))
                .sorted(Comparator.comparing(Agendamento::getDataHora)).toList();
        Map<Long, List<Agendamento>> vencidosPorPaciente = vencidos.stream().collect(Collectors.groupingBy(Agendamento::getPacienteId));
        List<FinanceiroVencidoDTO> relatorioVencidos = vencidosPorPaciente.entrySet().stream().map(entry -> {
            FinanceiroVencidoDTO dto = new FinanceiroVencidoDTO();
            List<Agendamento> ags = entry.getValue();
            String nomePaciente = pacientesMap.getOrDefault(entry.getKey(), "Paciente (ID: " + entry.getKey() + ")");
            dto.setPacienteNome(nomePaciente);
            dto.setValorTotal(ags.stream().map(Agendamento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add));
            LocalDateTime maisAntiga = ags.stream().map(Agendamento::getDataHora).min(LocalDateTime::compareTo).orElse(null);
            dto.setConsultaMaisAntiga(maisAntiga);
            if (maisAntiga != null) {
                dto.setDiasDeAtraso(ChronoUnit.DAYS.between(maisAntiga, agora));
            }
            return dto;
        }).sorted(Comparator.comparing(FinanceiroVencidoDTO::getConsultaMaisAntiga)).toList();
        List<FinanceiroAVencerDTO> relatorioAVencer = aVencer.stream().map(ag -> {
            FinanceiroAVencerDTO dto = new FinanceiroAVencerDTO();
            String nomePaciente = pacientesMap.getOrDefault(ag.getPacienteId(), "Paciente (ID: " + ag.getPacienteId() + ")");
            dto.setPacienteNome(nomePaciente);
            dto.setDataHora(ag.getDataHora());
            dto.setValor(ag.getValor());
            return dto;
        }).toList();
        RelatorioFinanceiroDTO relatorioFinal = new RelatorioFinanceiroDTO();
        relatorioFinal.setVencidos(relatorioVencidos);
        relatorioFinal.setAVencer(relatorioAVencer);
        return ResponseEntity.ok(relatorioFinal);
    }
}