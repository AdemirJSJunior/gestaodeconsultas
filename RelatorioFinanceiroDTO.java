package com.gestaodeh.gestaodeh.payload.response;
import java.util.List;
public class RelatorioFinanceiroDTO {
    private List<FinanceiroVencidoDTO> vencidos; private List<FinanceiroAVencerDTO> aVencer;
    public List<FinanceiroVencidoDTO> getVencidos() { return vencidos; } public void setVencidos(List<FinanceiroVencidoDTO> vencidos) { this.vencidos = vencidos; }
    public List<FinanceiroAVencerDTO> getAVencer() { return aVencer; } public void setAVencer(List<FinanceiroAVencerDTO> aVencer) { this.aVencer = aVencer; }
}