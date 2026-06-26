package com.sigep.services.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sigep.dto.request.ContinenteRequestDTO;
import com.sigep.dto.response.ApiResponseDTO;
import com.sigep.dto.response.ContinenteResponseDTO;
import com.sigep.dto.response.MetaResponseDTO;
import com.sigep.entities.ContinenteEntity;
import com.sigep.mappers.ContinenteMapper;
import com.sigep.repositories.ContinenteRepository;
import com.sigep.services.ContinenteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContinenteServiceImpl implements ContinenteService {

    private final ContinenteRepository continenteRepository;

    @Override
    public ResponseEntity<ApiResponseDTO> getAll() {
        List<ContinenteEntity> entities = null;

        try {
            entities = continenteRepository.getAll();
        } catch (Exception e) {
            MetaResponseDTO meta = MetaResponseDTO.builder()
                    .status("FAILURE")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error al recuperar la lista de continentes.")
                    .devMessage(e.getMessage())
                    .build();

            ApiResponseDTO response = ApiResponseDTO.builder()
                    .meta(meta)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (entities == null || entities.isEmpty()) {
            MetaResponseDTO meta = MetaResponseDTO.builder()
                    .status("SUCCESS")
                    .statusCode(HttpStatus.NO_CONTENT.value())
                    .message("No se encontraron registros de continentes.")
                    .build();

            ApiResponseDTO response = ApiResponseDTO.builder()
                    .meta(meta)
                    .build();

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        List<ContinenteResponseDTO> dtos = entities.stream()
                .map(ContinenteMapper::toResponseDTO)
                .toList();

        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status("SUCCESS")
                .statusCode(HttpStatus.OK.value())
                .message("Lista de continentes recuperada con éxito.")
                .build();

        ApiResponseDTO response = ApiResponseDTO.builder()
                .meta(meta)
                .data(dtos)
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public List<ContinenteResponseDTO> getAllActive() {
        List<ContinenteEntity> entities = continenteRepository.getAllActive();
        return entities.stream()
                .map(ContinenteMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ContinenteResponseDTO findById(Long id) {
        ContinenteEntity entity = continenteRepository.findByIdCustom(id);
        return ContinenteMapper.toResponseDTO(entity);
    }

    @Override
    public ContinenteResponseDTO findByName(String nombre) {
        ContinenteEntity entity = continenteRepository.findByNameCustom(nombre);
        return ContinenteMapper.toResponseDTO(entity);
    }

    @Override
    public List<ContinenteResponseDTO> findByPartialName(String nombre) {
        List<ContinenteEntity> entities = continenteRepository.findByPartialName(nombre);
        return entities.stream()
                .map(ContinenteMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ResponseEntity<ApiResponseDTO> create(ContinenteRequestDTO requestDTO) {
        int rowsAffected = 0;

        try {
            rowsAffected = continenteRepository.create(requestDTO.getNombre());
        } catch (Exception e) {
            MetaResponseDTO meta = MetaResponseDTO.builder()
                    .status("FAILURE")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error al intentar registrar el continente.")
                    .devMessage(e.getMessage())
                    .build();

            ApiResponseDTO response = ApiResponseDTO.builder()
                    .meta(meta)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (rowsAffected > 0) {
            MetaResponseDTO meta = MetaResponseDTO.builder()
                    .status("BAD REQUEST")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("No se pudo registrar el continente.")
                    .build();

            ApiResponseDTO response = ApiResponseDTO.builder()
                    .meta(meta)
                    .build();

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status("SUCCESS")
                .statusCode(HttpStatus.OK.value())
                .message("Continente registrado con éxito.")
                .build();

        ApiResponseDTO response = ApiResponseDTO.builder()
                .meta(meta)
                .data(requestDTO)
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ContinenteResponseDTO update(Long id, ContinenteRequestDTO requestDTO) {
        int rowsAffected = continenteRepository.update(id, requestDTO.getNombre());
        if (rowsAffected > 0) {
            ContinenteEntity entity = continenteRepository.findByIdCustom(id);
            return ContinenteMapper.toResponseDTO(entity);
        }
        return null;
    }

    @Override
    public boolean softDelete(Long id) {
        int rowsAffected = continenteRepository.softDeleteById(id);
        return rowsAffected > 0;
    }
}
