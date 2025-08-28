// Arquivo: src/main/java/com/gestaodeh/gestaodeh/model/AgendamentosSolicitacaoNf.java
package com.gestaodeh.gestaodeh.model;
import jakarta.persistence.*;
import java.io.Serializable;
@Entity @Table(name = "agendamentos_solicitacao_nf")
@IdClass(AgendamentosSolicitacaoNf.AgendamentosSolicitacaoNfId.class)
public class AgendamentosSolicitacaoNf {
    @Id private Long solicitacaoId;
    @Id private Long agendamentoId;
    public AgendamentosSolicitacaoNf() {}
    public AgendamentosSolicitacaoNf(Long solicitacaoId, Long agendamentoId) { this.solicitacaoId = solicitacaoId; this.agendamentoId = agendamentoId; }
    public Long getSolicitacaoId() { return solicitacaoId; } public void setSolicitacaoId(Long solicitacaoId) { this.solicitacaoId = solicitacaoId; }
    public Long getAgendamentoId() { return agendamentoId; } public void setAgendamentoId(Long agendamentoId) { this.agendamentoId = agendamentoId; }
    public static class AgendamentosSolicitacaoNfId implements Serializable {
        private Long solicitacaoId;
        private Long agendamentoId;
    }
}