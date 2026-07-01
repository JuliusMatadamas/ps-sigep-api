package com.sigep.services;

import java.util.List;

import com.sigep.dto.request.ContinenteRequestDTO;
import com.sigep.dto.response.ContinenteResponseDTO;

public interface ContinenteService {

    List<ContinenteResponseDTO> getAll();

    List<ContinenteResponseDTO> getAllActive();

    ContinenteResponseDTO findById(Long id);

    ContinenteResponseDTO findByName(String nombre);

    List<ContinenteResponseDTO> findByPartialName(String nombre);

    ContinenteResponseDTO create(ContinenteRequestDTO requestDTO);

    ContinenteResponseDTO update(Long id, ContinenteRequestDTO requestDTO);

    void softDelete(Long id);
}
