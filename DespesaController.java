// Arquivo: src/main/java/com/gestaodeh/gestaodeh/controller/DespesaController.java
package com.gestaodeh.gestaodeh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestaodeh.gestaodeh.model.Despesa;
import com.gestaodeh.gestaodeh.model.DespesaAnexo;
import com.gestaodeh.gestaodeh.repository.DespesaRepository;
import com.gestaodeh.gestaodeh.security.services.UserDetailsImpl;
import com.gestaodeh.gestaodeh.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/despesas")
@CrossOrigin(origins = "*")
public class DespesaController {
    @Autowired private DespesaRepository despesaRepository;
    @Autowired private FileStorageService fileStorageService;
    @Autowired private ObjectMapper objectMapper;

    private Long getUserIdLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @GetMapping
    public List<Despesa> listarDespesas() {
        return despesaRepository.findByUserId(getUserIdLogado());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Despesa> buscarPorId(@PathVariable Long id) {
        return despesaRepository.findById(id)
            .filter(despesa -> despesa.getUserId().equals(getUserIdLogado()))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Despesa criarDespesa(@RequestPart("despesa") String despesaJson, @RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException {
        Despesa despesa = objectMapper.readValue(despesaJson, Despesa.class);
        despesa.setUserId(getUserIdLogado());

        if (files != null) {
            Set<DespesaAnexo> anexos = new HashSet<>();
            for (MultipartFile file : files) {
                String fileName = fileStorageService.storeFile(file);
                DespesaAnexo anexo = new DespesaAnexo();
                anexo.setFileName(file.getOriginalFilename());
                anexo.setFilePath(fileName);
                anexo.setDespesa(despesa);
                anexos.add(anexo);
            }
            despesa.setAnexos(anexos);
        }
        return despesaRepository.save(despesa);
    }
    
    @GetMapping("/anexos/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarDespesa(@PathVariable Long id) {
        return despesaRepository.findById(id).map(despesa -> {
            if (!despesa.getUserId().equals(getUserIdLogado())) {
                return ResponseEntity.status(403).build();
            }
            despesaRepository.delete(despesa);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}