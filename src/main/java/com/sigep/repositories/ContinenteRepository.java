package com.sigep.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.sigep.entities.ContinenteEntity;

public interface ContinenteRepository extends JpaRepository<ContinenteEntity, Long> {
    @Transactional
    @Query(value = """
        SELECT
            id,
            nombre,
            created_at,
            deleted_at,
            updated_at
        FROM
            continentes
        """, nativeQuery = true)
    List<ContinenteEntity> getAll();

    @Transactional
    @Query(value = """
        SELECT
            id,
            nombre,
            created_at,
            deleted_at,
            updated_at
        FROM
            continentes
        WHERE
            deleted_at IS NULL
        """, nativeQuery = true)
    List<ContinenteEntity> getAllActive();

    @Transactional
    @Query(value = """
        SELECT
            id,
            nombre,
            created_at,
            deleted_at,
            updated_at
        FROM
            continentes
        WHERE
            id = :id
        """, nativeQuery = true)
    ContinenteEntity findByIdCustom(Long id);

    @Query(value = """
        SELECT
            id,
            nombre,
            created_at,
            deleted_at,
            updated_at
        FROM
            continentes
        WHERE
            nombre = :nombre
        """, nativeQuery = true)
    ContinenteEntity findByNameCustom(String nombre);

    @Query(value = """
        SELECT
            id,
            nombre,
            created_at,
            deleted_at,
            updated_at
        FROM
            continentes
        WHERE
            nombre LIKE '%:nombre%'
        """, nativeQuery = true)
    List<ContinenteEntity> findByPartialName(String nombre);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO continentes (nombre) VALUES (LOWER(TRIM(:nombre)))
        """, nativeQuery = true)
    int create(String nombre);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE continentes SET nombre = LOWER(TRIM(:nombre)) WHERE id = :id
        """, nativeQuery = true)
    int update(Long id, String nombre);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE continentes SET deleted_at = CURRENT_TIMESTAMP WHERE id = :id
        """, nativeQuery = true)
    int softDeleteById(Long id);
}

