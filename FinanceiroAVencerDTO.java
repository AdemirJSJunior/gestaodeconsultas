package com.gestaodeh.gestaodeh.payload.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class FinanceiroAVencerDTO {
    private String pacienteNome; private BigDecimal valor; private LocalDateTime dataHora;
    public String getPacienteNome() { return pacienteNome; } public void setPacienteNome(String pacienteNome) { this.pacienteNome = pacienteNome; }
    public BigDecimal getValor() { return valor; } public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDateTime getDataHora() { return dataHora; } public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}