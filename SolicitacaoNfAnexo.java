// Arquivo: src/main/java/com/gestaodeh/gestaodeh/model/SolicitacaoNfAnexo.java
package com.gestaodeh.gestaodeh.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
@Entity @Table(name = "solicitacao_nf_anexos")
public class SolicitacaoNfAnexo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String fileName;
    @Column(nullable = false) private String filePath;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "solicitacao_id", nullable = false)
    @JsonBackReference
    private SolicitacaoNF solicitacao;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; } public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; } public void setFilePath(String filePath) { this.filePath = filePath; }
    public SolicitacaoNF getSolicitacao() { return solicitacao; } public void setSolicitacao(SolicitacaoNF solicitacao) { this.solicitacao = solicitacao; }
}