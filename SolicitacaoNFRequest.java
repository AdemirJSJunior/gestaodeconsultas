package com.gestaodeh.gestaodeh.payload.request;
import java.util.List;
public class SolicitacaoNFRequest {
    private Long pacienteId;
    private String pacienteNomeNf;
    private String pacienteCpfNf;
    private String pacienteEnderecoNf;
    private List<Long> agendamentoIds;
    public Long getPacienteId() { return pacienteId; } public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public String getPacienteNomeNf() { return pacienteNomeNf; } public void setPacienteNomeNf(String pacienteNomeNf) { this.pacienteNomeNf = pacienteNomeNf; }
    public String getPacienteCpfNf() { return pacienteCpfNf; } public void setPacienteCpfNf(String pacienteCpfNf) { this.pacienteCpfNf = pacienteCpfNf; }
    public String getPacienteEnderecoNf() { return pacienteEnderecoNf; } public void setPacienteEnderecoNf(String pacienteEnderecoNf) { this.pacienteEnderecoNf = pacienteEnderecoNf; }
    public List<Long> getAgendamentoIds() { return agendamentoIds; } public void setAgendamentoIds(List<Long> agendamentoIds) { this.agendamentoIds = agendamentoIds; }
}