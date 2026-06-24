package com.sigep.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigep.dto.request.ContinenteRequestDTO;
import com.sigep.dto.response.ApiResponseDTO;
import com.sigep.dto.response.ContinenteResponseDTO;
import com.sigep.services.ContinenteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/continentes")
@RequiredArgsConstructor
public class ContinenteController {

    private final ContinenteService continenteService;

    @GetMapping("/v1/todos")
    public ResponseEntity<ApiResponseDTO> getAll() {
        List<ContinenteResponseDTO> continentes = continenteService.getAll();
        ApiResponseDTO response = ApiResponseDTO.builder()
                .data(continentes)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/activos")
    public ResponseEntity<ApiResponseDTO> getAllActive() {
        List<ContinenteResponseDTO> continentes = continenteService.getAllActive();
        ApiResponseDTO response = ApiResponseDTO.builder()
                .data(continentes)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/{id}")
    public ResponseEntity<ApiResponseDTO> findById(@PathVariable Long id) {
        ContinenteResponseDTO continente = continenteService.findById(id);
        ApiResponseDTO response = ApiResponseDTO.builder()
                .data(continente)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/nombre/{nombre}")
    public ResponseEntity<ApiResponseDTO> findByName(@PathVariable String nombre) {
        ContinenteResponseDTO continente = continenteService.findByName(nombre);
        ApiResponseDTO response = ApiResponseDTO.builder()
                .data(continente)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/buscar/{nombre}")
    public ResponseEntity<ApiResponseDTO> findByPartialName(@PathVariable String nombre) {
        List<ContinenteResponseDTO> continentes = continenteService.findByPartialName(nombre);
        ApiResponseDTO response = ApiResponseDTO.builder()
                .data(continentes)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/v1/crear")
    public ResponseEntity<ApiResponseDTO> create(@RequestBody ContinenteRequestDTO requestDTO) {
        ContinenteResponseDTO continente = continenteService.create(requestDTO);
        ApiResponseDTO response = ApiResponseDTO.builder()
                .data(continente)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/v1/actualizar/{id}")
    public ResponseEntity<ApiResponseDTO> update(@PathVariable Long id, @RequestBody ContinenteRequestDTO requestDTO) {
        ContinenteResponseDTO continente = continenteService.update(id, requestDTO);
        ApiResponseDTO response = ApiResponseDTO.builder()
                .data(continente)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/v1/eliminar/{id}")
    public ResponseEntity<ApiResponseDTO> softDelete(@PathVariable Long id) {
        boolean deleted = continenteService.softDelete(id);
        ApiResponseDTO response = ApiResponseDTO.builder()
                .data(deleted)
                .build();
        return ResponseEntity.ok(response);
    }
}
