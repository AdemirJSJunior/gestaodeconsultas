// Arquivo: src/main/java/com/gestaodeh/gestaodeh/model/Paciente.java
package com.gestaodeh.gestaodeh.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name = "pacientes")
public class Paciente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String nomeCompleto;
    @Column(nullable = false) private LocalDate dataNascimento;
    private String cpf; private String endereco; private String telefone; private String email;
    private String nomeResponsavel; private String cpfResponsavel;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getNomeCompleto() { return nomeCompleto; } public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public LocalDate getDataNascimento() { return dataNascimento; } public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getCpf() { return cpf; } public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEndereco() { return endereco; } public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTelefone() { return telefone; } public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getNomeResponsavel() { return nomeResponsavel; } public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }
    public String getCpfResponsavel() { return cpfResponsavel; } public void setCpfResponsavel(String cpfResponsavel) { this.cpfResponsavel = cpfResponsavel; }
}