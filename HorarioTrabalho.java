// Arquivo: src/main/java/com/gestaodeh/gestaodeh/model/HorarioTrabalho.java
package com.gestaodeh.gestaodeh.model;
import jakarta.persistence.*;
import java.time.LocalTime;
@Entity @Table(name = "horarios_trabalho")
public class HorarioTrabalho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    @Enumerated(EnumType.STRING) private DiaSemana diaSemana;
    @Column(nullable = false) private LocalTime horaInicio;
    @Column(nullable = false) private LocalTime horaFim;
    public enum DiaSemana { Domingo, Segunda, Terca, Quarta, Quinta, Sexta, Sabado }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public DiaSemana getDiaSemana() { return diaSemana; } public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; } public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; } public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
}
