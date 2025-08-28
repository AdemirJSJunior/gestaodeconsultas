package com.gestaodeh.gestaodeh.payload.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class FinanceiroVencidoDTO {
    private String pacienteNome; private BigDecimal valorTotal;
    private LocalDateTime consultaMaisAntiga; private long diasDeAtraso;
    public String getPacienteNome() { return pacienteNome; } public void setPacienteNome(String pacienteNome) { this.pacienteNome = pacienteNome; }
    public BigDecimal getValorTotal() { return valorTotal; } public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public LocalDateTime getConsultaMaisAntiga() { return consultaMaisAntiga; } public void setConsultaMaisAntiga(LocalDateTime consultaMaisAntiga) { this.consultaMaisAntiga = consultaMaisAntiga; }
    public long getDiasDeAtraso() { return diasDeAtraso; } public void setDiasDeAtraso(long diasDeAtraso) { this.diasDeAtraso = diasDeAtraso; }
}