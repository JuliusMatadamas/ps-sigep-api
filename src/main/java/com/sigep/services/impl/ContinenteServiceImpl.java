package com.sigep.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sigep.dto.request.ContinenteRequestDTO;
import com.sigep.dto.response.ContinenteResponseDTO;
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
    public List<ContinenteResponseDTO> getAll() {
        List<ContinenteEntity> entities = continenteRepository.getAll();
        return entities.stream()
                .map(ContinenteMapper::toResponseDTO)
                .toList();
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
    public ContinenteResponseDTO create(ContinenteRequestDTO requestDTO) {
        int rowsAffected = continenteRepository.create(requestDTO.getNombre());
        if (rowsAffected > 0) {
            ContinenteEntity entity = continenteRepository.findByNameCustom(requestDTO.getNombre().toLowerCase().trim());
            return ContinenteMapper.toResponseDTO(entity);
        }
        return null;
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
