package com.gestaodeh.gestaodeh.payload.response;
import java.util.List;
public class JwtResponse {
    private String token; private String type = "Bearer"; private Long id;
    private String email; private String nome; private List<String> roles;
    public JwtResponse(String accessToken, Long id, String email, String nome, List<String> roles) {
        this.token = accessToken; this.id = id; this.email = email; this.nome = nome; this.roles = roles;
    }
    public String getToken() { return token; } public void setToken(String token) { this.token = token; }
    public String getType() { return type; } public void setType(String type) { this.type = type; }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getNome() { return nome; } public void setNome(String nome) { this.nome = nome; }
    public List<String> getRoles() { return roles; }
}