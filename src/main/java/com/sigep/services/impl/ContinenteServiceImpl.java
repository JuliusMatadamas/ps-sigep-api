package com.sigep.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sigep.constants.AppConstants;
import com.sigep.dto.request.ContinenteRequestDTO;
import com.sigep.dto.response.ContinenteResponseDTO;
import com.sigep.entities.ContinenteEntity;
import com.sigep.exceptions.BadRequestException;
import com.sigep.exceptions.DuplicateResourceException;
import com.sigep.exceptions.NoContentException;
import com.sigep.exceptions.ResourceNotFoundException;
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
    public List<ContinenteResponseDTO> getAll() {
        LogUtil.info("Obteniendo todos los continentes");
        List<ContinenteEntity> entities = continenteRepository.getAll();

        if (entities.isEmpty()) {
            LogUtil.warn("No se encontraron registros de continentes");
            throw new NoContentException("No se encontraron registros de continentes.");
        }

        LogUtil.info(AppConstants.MSG_RETORNADOS + entities.size() + " continentes");
        return toResponseList(entities);
    }

    @Override
    public List<ContinenteResponseDTO> getAllActive() {
        LogUtil.info("Obteniendo continentes activos");
        List<ContinenteEntity> entities = continenteRepository.getAllActive();

        if (entities.isEmpty()) {
            LogUtil.warn("No se encontraron continentes activos");
            throw new NoContentException("No se encontraron continentes activos.");
        }

        LogUtil.info(AppConstants.MSG_RETORNADOS + entities.size() + " continentes activos");
        return toResponseList(entities);
    }

    @Override
    public ContinenteResponseDTO findById(Long id) {
        LogUtil.info("Buscando continente por id: " + id);
        ContinenteEntity entity = continenteRepository.findByIdCustom(id);

        if (entity == null) {
            LogUtil.warn(AppConstants.MSG_CONTINENTE_NO_ENCONTRADO_ID + id);
            throw new NoContentException(AppConstants.MSG_CONTINENTE_NO_ENCONTRADO);
        }

        LogUtil.info("Continente encontrado con id: " + id);
        return ContinenteMapper.toResponseDTO(entity);
    }

    @Override
    public ContinenteResponseDTO findByName(String nombre) {
        LogUtil.info("Buscando continente por nombre: " + nombre);
        ContinenteEntity entity = continenteRepository.findByNameCustom(
                nombre.toLowerCase().trim()
        );

        if (entity == null) {
            LogUtil.warn("Continente no encontrado con nombre: " + nombre);
            throw new NoContentException(AppConstants.MSG_CONTINENTE_NO_ENCONTRADO);
        }

        LogUtil.info("Continente encontrado con nombre: " + nombre);
        return ContinenteMapper.toResponseDTO(entity);
    }

    @Override
    public List<ContinenteResponseDTO> findByPartialName(String nombre) {
        LogUtil.info("Buscando continentes por nombre parcial: " + nombre);

        if (!ValidationUtil.isValidString(nombre, 1)) {
            LogUtil.warn("Nombre de busqueda invalido");
            throw new BadRequestException("El nombre de busqueda es invalido.");
        }

        List<ContinenteEntity> entities = continenteRepository.findByPartialName(
                nombre.toLowerCase().trim()
        );

        if (entities.isEmpty()) {
            LogUtil.warn("No se encontraron continentes con nombre parcial: " + nombre);
            throw new NoContentException("No se encontraron continentes con el nombre proporcionado.");
        }

        LogUtil.info(AppConstants.MSG_RETORNADOS + entities.size() + " continentes por busqueda parcial");
        return toResponseList(entities);
    }

    @Override
    public ContinenteResponseDTO create(ContinenteRequestDTO requestDTO) {
        LogUtil.info("Iniciando registro de continente: " + requestDTO.getNombre());
        validateNombre(requestDTO.getNombre());

        ContinenteEntity existing = continenteRepository.findByNameCustom(
                requestDTO.getNombre().toLowerCase().trim()
        );

        if (existing != null) {
            LogUtil.warn(AppConstants.MSG_CONTINENTE_YA_REGISTRADO_PREFIX + requestDTO.getNombre() + "' ya existe");
            throw new DuplicateResourceException(
                    AppConstants.MSG_CONTINENTE_YA_REGISTRADO_PREFIX + requestDTO.getNombre() + "' ya esta registrado."
            );
        }

        int rowsAffected = continenteRepository.create(requestDTO.getNombre());
        LogUtil.logVar(AppConstants.LOG_VAR_ROWS_AFFECTED, rowsAffected);

        if (rowsAffected == 0) {
            LogUtil.warn("No se pudo registrar el continente");
            throw new BadRequestException("No se pudo registrar el continente.");
        }

        LogUtil.info("Continente registrado con exito");
        ContinenteEntity createdEntity = continenteRepository.findByNameCustom(
                requestDTO.getNombre().toLowerCase().trim()
        );
        return ContinenteMapper.toResponseDTO(createdEntity);
    }

    @Override
    public ContinenteResponseDTO update(Long id, ContinenteRequestDTO requestDTO) {
        LogUtil.info("Iniciando actualizacion de continente id: " + id);
        validateNombre(requestDTO.getNombre());

        ContinenteEntity existingById = continenteRepository.findByIdCustom(id);
        if (existingById == null) {
            LogUtil.warn(AppConstants.MSG_CONTINENTE_NO_ENCONTRADO_ID + id);
            throw new ResourceNotFoundException(AppConstants.MSG_CONTINENTE_NO_ENCONTRADO);
        }

        ContinenteEntity existingByName = continenteRepository.findByNameCustom(
                requestDTO.getNombre().toLowerCase().trim()
        );

        if (existingByName != null && !existingByName.getId().equals(id)) {
            LogUtil.warn(AppConstants.MSG_CONTINENTE_YA_REGISTRADO_PREFIX + requestDTO.getNombre() + "' ya esta registrado");
            throw new DuplicateResourceException(
                    AppConstants.MSG_CONTINENTE_YA_REGISTRADO_PREFIX + requestDTO.getNombre() + "' ya esta registrado."
            );
        }

        int rowsAffected = continenteRepository.update(id, requestDTO.getNombre());
        LogUtil.logVar(AppConstants.LOG_VAR_ROWS_AFFECTED, rowsAffected);

        if (rowsAffected == 0) {
            LogUtil.warn("No se pudo actualizar el continente");
            throw new BadRequestException("No se pudo actualizar el continente.");
        }

        LogUtil.info("Continente actualizado con exito");
        ContinenteEntity updatedEntity = continenteRepository.findByIdCustom(id);
        return ContinenteMapper.toResponseDTO(updatedEntity);
    }

    @Override
    public void softDelete(Long id) {
        LogUtil.info("Iniciando eliminacion logica de continente id: " + id);

        ContinenteEntity existing = continenteRepository.findByIdCustom(id);
        if (existing == null) {
            LogUtil.warn(AppConstants.MSG_CONTINENTE_NO_ENCONTRADO_ID + id);
            throw new ResourceNotFoundException(AppConstants.MSG_CONTINENTE_NO_ENCONTRADO);
        }

        int rowsAffected = continenteRepository.softDeleteById(id);
        LogUtil.logVar(AppConstants.LOG_VAR_ROWS_AFFECTED, rowsAffected);

        if (rowsAffected == 0) {
            LogUtil.warn("No se pudo eliminar el continente");
            throw new BadRequestException("No se pudo eliminar el continente.");
        }

        LogUtil.info("Continente eliminado con exito");
    }

    private void validateNombre(String nombre) {
        if (!ValidationUtil.isValidString(nombre)) {
            LogUtil.warn("Nombre de continente invalido");
            throw new BadRequestException("Nombre de continente invalido.");
        }
    }

    private List<ContinenteResponseDTO> toResponseList(List<ContinenteEntity> entities) {
        return entities.stream()
                .map(ContinenteMapper::toResponseDTO)
                .toList();
    }
}
