package com.sigep.services.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sigep.constants.AppConstants;
import com.sigep.dto.request.ContinenteRequestDTO;
import com.sigep.dto.response.ApiResponseDTO;
import com.sigep.dto.response.ContinenteResponseDTO;
import com.sigep.dto.response.MetaResponseDTO;
import com.sigep.entities.ContinenteEntity;
import com.sigep.mappers.ContinenteMapper;
import com.sigep.repositories.ContinenteRepository;
import com.sigep.services.ContinenteService;
import com.sigep.utils.LogUtil;
import com.sigep.utils.ValidationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContinenteServiceImpl implements ContinenteService {

    private final ContinenteRepository continenteRepository;

    @Override
    public ResponseEntity<ApiResponseDTO> getAll() {
        List<ContinenteEntity> entities;

        try {
            entities = continenteRepository.getAll();
        } catch (Exception e) {
            MetaResponseDTO meta = MetaResponseDTO.builder()
                    .status(AppConstants.FAILURE)
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
                    .status(AppConstants.SUCCESS)
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
                .status(AppConstants.SUCCESS)
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
        LogUtil.info("Iniciando registro de continente: " + requestDTO.getNombre());

        if (!ValidationUtil.isValidString(requestDTO.getNombre())) {
            LogUtil.warn("Nombre de continente inválido");
            MetaResponseDTO meta = MetaResponseDTO.builder()
                    .status(AppConstants.BAD_REQUEST)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Nombre de continente inválido.")
                    .build();

            ApiResponseDTO response = ApiResponseDTO.builder()
                    .meta(meta)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ContinenteEntity existing;

        try {
            existing = continenteRepository.findByNameCustom(
                requestDTO.getNombre().toLowerCase().trim()
            );
        } catch (Exception e) {
            LogUtil.error("Excepción al validar existencia de continente", e);
            MetaResponseDTO meta = MetaResponseDTO.builder()
                    .status(AppConstants.FAILURE)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error al intentar validar existencia del continente.")
                    .devMessage(e.getMessage())
                    .build();

            ApiResponseDTO response = ApiResponseDTO.builder()
                    .meta(meta)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (existing != null) {
            LogUtil.warn("El continente '" + requestDTO.getNombre() + "' ya existe");
            MetaResponseDTO meta = MetaResponseDTO.builder()
                    .status(AppConstants.BAD_REQUEST)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("El continente '" + requestDTO.getNombre() + "' ya esta registrado.")
                    .build();

            ApiResponseDTO response = ApiResponseDTO.builder()
                    .meta(meta)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        int rowsAffected;

        try {
            rowsAffected = continenteRepository.create(requestDTO.getNombre());
            LogUtil.var("rowsAffected", rowsAffected);
        } catch (Exception e) {
            LogUtil.error("Excepción al registrar continente", e);
            MetaResponseDTO meta = MetaResponseDTO.builder()
                    .status(AppConstants.FAILURE)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error al intentar registrar el continente.")
                    .devMessage(e.getMessage())
                    .build();

            ApiResponseDTO response = ApiResponseDTO.builder()
                    .meta(meta)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (rowsAffected == 0) {
            LogUtil.warn("No se pudo registrar el continente");
            MetaResponseDTO meta = MetaResponseDTO.builder()
                    .status(AppConstants.BAD_REQUEST)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("No se pudo registrar el continente.")
                    .build();

            ApiResponseDTO response = ApiResponseDTO.builder()
                    .meta(meta)
                    .build();

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        LogUtil.info("Continente registrado con éxito");

        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status(AppConstants.SUCCESS)
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
