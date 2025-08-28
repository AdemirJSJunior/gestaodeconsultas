// Arquivo: src/main/java/com/gestaodeh/gestaodeh/model/Despesa.java
package com.gestaodeh.gestaodeh.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
@Entity @Table(name = "despesas")
public class Despesa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String descricao;
    @Column(nullable = false) private BigDecimal valor;
    @Column(nullable = false) private LocalDate dataDespesa;
    private String categoria;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatusDespesa status;
    @OneToMany(mappedBy = "despesa", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<DespesaAnexo> anexos = new HashSet<>();
    public enum StatusDespesa { Pago, Aguardando }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getDescricao() { return descricao; } public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValor() { return valor; } public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getDataDespesa() { return dataDespesa; } public void setDataDespesa(LocalDate dataDespesa) { this.dataDespesa = dataDespesa; }
    public String getCategoria() { return categoria; } public void setCategoria(String categoria) { this.categoria = categoria; }
    public StatusDespesa getStatus() { return status; } public void setStatus(StatusDespesa status) { this.status = status; }
    public Set<DespesaAnexo> getAnexos() { return anexos; } public void setAnexos(Set<DespesaAnexo> anexos) { this.anexos = anexos; }
}