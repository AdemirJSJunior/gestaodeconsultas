// Arquivo: src/main/java/com/gestaodeh/gestaodeh/model/Agendamento.java
package com.gestaodeh.gestaodeh.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name = "agendamentos")
public class Agendamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private Long pacienteId;
    @Column(nullable = false) private LocalDateTime dataHora;
    @Column(nullable = false) private BigDecimal valor;
    @Enumerated(EnumType.STRING) private StatusAgendamento status;
    private String observacoes;
    public enum StatusAgendamento { Em_Aberto, Pago, Cancelado }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public Long getPacienteId() { return pacienteId; } public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public LocalDateTime getDataHora() { return dataHora; } public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public BigDecimal getValor() { return valor; } public void setValor(BigDecimal valor) { this.valor = valor; }
    public StatusAgendamento getStatus() { return status; } public void setStatus(StatusAgendamento status) { this.status = status; }
    public String getObservacoes() { return observacoes; } public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}