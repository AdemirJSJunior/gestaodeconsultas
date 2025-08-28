// Arquivo: src/main/java/com/gestaodeh/gestaodeh/controller/PacienteController.java
package com.gestaodeh.gestaodeh.controller;

import com.gestaodeh.gestaodeh.model.Paciente;
import com.gestaodeh.gestaodeh.repository.PacienteRepository;
import com.gestaodeh.gestaodeh.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {
    @Autowired
    private PacienteRepository pacienteRepository;

    private Long getUserIdLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findByUserId(getUserIdLogado());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        return pacienteRepository.findById(id)
            .filter(p -> p.getUserId().equals(getUserIdLogado()))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Paciente criarPaciente(@RequestBody Paciente paciente) {
        paciente.setUserId(getUserIdLogado());
        return pacienteRepository.save(paciente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizarPaciente(@PathVariable Long id, @RequestBody Paciente detalhes) {
        return (ResponseEntity<Paciente>) pacienteRepository.findById(id)
            .map(paciente -> {
                if (!paciente.getUserId().equals(getUserIdLogado())) {
                    return ResponseEntity.status(403).build();
                }
                paciente.setNomeCompleto(detalhes.getNomeCompleto());
                paciente.setDataNascimento(detalhes.getDataNascimento());
                paciente.setCpf(detalhes.getCpf());
                paciente.setEndereco(detalhes.getEndereco());
                paciente.setTelefone(detalhes.getTelefone());
                paciente.setEmail(detalhes.getEmail());
                paciente.setNomeResponsavel(detalhes.getNomeResponsavel());
                paciente.setCpfResponsavel(detalhes.getCpfResponsavel());
                return ResponseEntity.ok(pacienteRepository.save(paciente));
            }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPaciente(@PathVariable Long id) {
        return pacienteRepository.findById(id).map(paciente -> {
            if (!paciente.getUserId().equals(getUserIdLogado())) {
                return ResponseEntity.status(403).build();
            }
            pacienteRepository.delete(paciente);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}