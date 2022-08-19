package com.fonkwill.fogstorage.middleware.shared.service;

import com.fonkwill.fogstorage.middleware.shared.domain.Chunk;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Chunk}.
 */
public interface ChunkService {

    /**
     * Save a chunk.
     *
     * @param chunk the entity to save.
     * @return the persisted entity.
     */
    Chunk save(Chunk chunk);

    /**
     * Get all the chunks.
     *
     * @return the list of entities.
     */
    List<Chunk> findAll();


    /**
     * Get the "id" chunk.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Chunk> findOne(Long id);

    /**
     * Delete the "id" chunk.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
