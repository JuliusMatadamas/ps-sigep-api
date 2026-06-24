package com.sigep.mappers;

import com.sigep.dto.request.ContinenteRequestDTO;
import com.sigep.dto.response.ContinenteResponseDTO;
import com.sigep.entities.ContinenteEntity;

public class ContinenteMapper {

    private ContinenteMapper() {
        throw new IllegalStateException("Clase de utilidad, no debe ser instanciada.");
    }

    public static ContinenteEntity toEntity(ContinenteRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        return ContinenteEntity.builder()
                .nombre(requestDTO.getNombre())
                .build();
    }

    public static ContinenteResponseDTO toResponseDTO(ContinenteEntity entity) {
        if (entity == null) {
            return null;
        }
        return ContinenteResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }
}
