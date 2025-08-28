// Arquivo: src/main/java/com/gestaodeh/gestaodeh/model/DespesaAnexo.java
package com.gestaodeh.gestaodeh.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
@Entity @Table(name = "despesa_anexos")
public class DespesaAnexo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String fileName;
    @Column(nullable = false) private String filePath;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "despesa_id", nullable = false)
    @JsonBackReference
    private Despesa despesa;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; } public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; } public void setFilePath(String filePath) { this.filePath = filePath; }
    public Despesa getDespesa() { return despesa; } public void setDespesa(Despesa despesa) { this.despesa = despesa; }
}