// Arquivo: src/main/java/com/gestaodeh/gestaodeh/model/SolicitacaoNF.java
package com.gestaodeh.gestaodeh.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@Entity @Table(name = "solicitacoes_nf")
public class SolicitacaoNF {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private Long pacienteId;
    private LocalDateTime dataSolicitacao = LocalDateTime.now();
    @Column(nullable = false) private BigDecimal valorTotal;
    @Enumerated(EnumType.STRING) private StatusNF status;
    @Column(nullable = false) private String pacienteNomeNf;
    private String pacienteCpfNf;
    private String pacienteEnderecoNf;
    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<SolicitacaoNfAnexo> anexos = new HashSet<>();
    public enum StatusNF { Pendente, Enviada }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public Long getPacienteId() { return pacienteId; } public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; } public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
    public BigDecimal getValorTotal() { return valorTotal; } public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public StatusNF getStatus() { return status; } public void setStatus(StatusNF status) { this.status = status; }
    public String getPacienteNomeNf() { return pacienteNomeNf; } public void setPacienteNomeNf(String pacienteNomeNf) { this.pacienteNomeNf = pacienteNomeNf; }
    public String getPacienteCpfNf() { return pacienteCpfNf; } public void setPacienteCpfNf(String pacienteCpfNf) { this.pacienteCpfNf = pacienteCpfNf; }
    public String getPacienteEnderecoNf() { return pacienteEnderecoNf; } public void setPacienteEnderecoNf(String pacienteEnderecoNf) { this.pacienteEnderecoNf = pacienteEnderecoNf; }
    public Set<SolicitacaoNfAnexo> getAnexos() { return anexos; } public void setAnexos(Set<SolicitacaoNfAnexo> anexos) { this.anexos = anexos; }
}