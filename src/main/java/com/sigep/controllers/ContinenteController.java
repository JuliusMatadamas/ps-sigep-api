package com.sigep.controllers;

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
import com.sigep.services.ContinenteService;
import com.sigep.utils.ApiResponseBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/continentes")
@RequiredArgsConstructor
public class ContinenteController {

    private final ContinenteService continenteService;

    @GetMapping("/v1/todos")
    public ResponseEntity<ApiResponseDTO> getAll() {
        return ApiResponseBuilder.ok(
                continenteService.getAll(),
                "Lista de continentes recuperada con exito."
        );
    }

    @GetMapping("/v1/activos")
    public ResponseEntity<ApiResponseDTO> getAllActive() {
        return ApiResponseBuilder.ok(
                continenteService.getAllActive(),
                "Lista de continentes activos recuperada con exito."
        );
    }

    @GetMapping("/v1/{id}")
    public ResponseEntity<ApiResponseDTO> findById(@PathVariable Long id) {
        return ApiResponseBuilder.ok(
                continenteService.findById(id),
                "Continente encontrado con exito."
        );
    }

    @GetMapping("/v1/nombre/{nombre}")
    public ResponseEntity<ApiResponseDTO> findByName(@PathVariable String nombre) {
        return ApiResponseBuilder.ok(
                continenteService.findByName(nombre),
                "Continente encontrado con exito."
        );
    }

    @GetMapping("/v1/buscar/{nombre}")
    public ResponseEntity<ApiResponseDTO> findByPartialName(@PathVariable String nombre) {
        return ApiResponseBuilder.ok(
                continenteService.findByPartialName(nombre),
                "Lista de continentes recuperada con exito."
        );
    }

    @PostMapping("/v1/crear")
    public ResponseEntity<ApiResponseDTO> create(@RequestBody ContinenteRequestDTO requestDTO) {
        return ApiResponseBuilder.created(
                continenteService.create(requestDTO),
                "Continente registrado con exito."
        );
    }

    @PutMapping("/v1/actualizar/{id}")
    public ResponseEntity<ApiResponseDTO> update(
            @PathVariable Long id,
            @RequestBody ContinenteRequestDTO requestDTO
    ) {
        return ApiResponseBuilder.ok(
                continenteService.update(id, requestDTO),
                "Continente actualizado con exito."
        );
    }

    @DeleteMapping("/v1/eliminar/{id}")
    public ResponseEntity<ApiResponseDTO> softDelete(@PathVariable Long id) {
        continenteService.softDelete(id);
        return ApiResponseBuilder.ok("Continente eliminado con exito.");
    }
}
