// Arquivo: src/main/java/com/gestaodeh/gestaodeh/controller/HorarioTrabalhoController.java
package com.gestaodeh.gestaodeh.controller;

import com.gestaodeh.gestaodeh.model.HorarioTrabalho;
import com.gestaodeh.gestaodeh.repository.HorarioTrabalhoRepository;
import com.gestaodeh.gestaodeh.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/horarios-trabalho")
@CrossOrigin(origins = "*")
public class HorarioTrabalhoController {
    @Autowired
    private HorarioTrabalhoRepository horarioTrabalhoRepository;

    private Long getUserIdLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public List<HorarioTrabalho> listarHorarios() {
        return horarioTrabalhoRepository.findByUserId(getUserIdLogado());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioTrabalho> buscarPorId(@PathVariable Long id) {
        return horarioTrabalhoRepository.findById(id)
            .filter(h -> h.getUserId().equals(getUserIdLogado()))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public HorarioTrabalho criarHorario(@RequestBody HorarioTrabalho horario) {
        horario.setUserId(getUserIdLogado());
        return horarioTrabalhoRepository.save(horario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioTrabalho> atualizarHorario(@PathVariable Long id, @RequestBody HorarioTrabalho detalhes) {
        return (ResponseEntity<HorarioTrabalho>) horarioTrabalhoRepository.findById(id).map(horario -> {
            if (!horario.getUserId().equals(getUserIdLogado())) {
                return ResponseEntity.status(403).build();
            }
            horario.setDiaSemana(detalhes.getDiaSemana());
            horario.setHoraInicio(detalhes.getHoraInicio());
            horario.setHoraFim(detalhes.getHoraFim());
            return ResponseEntity.ok(horarioTrabalhoRepository.save(horario));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarHorario(@PathVariable Long id) {
        return horarioTrabalhoRepository.findById(id).map(horario -> {
            if (!horario.getUserId().equals(getUserIdLogado())) {
                return ResponseEntity.status(403).build();
            }
            horarioTrabalhoRepository.delete(horario);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}