package com.fonkwill.fogstorage.middleware.service.impl;

import com.fonkwill.fogstorage.middleware.service.ChunkService;
import com.fonkwill.fogstorage.middleware.domain.Chunk;
import com.fonkwill.fogstorage.middleware.repository.ChunkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Chunk}.
 */
@Service
@Transactional
public class ChunkServiceImpl implements ChunkService {

    private final Logger log = LoggerFactory.getLogger(ChunkServiceImpl.class);

    private final ChunkRepository chunkRepository;

    public ChunkServiceImpl(ChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }

    /**
     * Save a chunk.
     *
     * @param chunk the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Chunk save(Chunk chunk) {
        log.debug("Request to save Chunk : {}", chunk);
        return chunkRepository.save(chunk);
    }

    /**
     * Get all the chunks.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Chunk> findAll() {
        log.debug("Request to get all Chunks");
        return chunkRepository.findAll();
    }


    /**
     * Get one chunk by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Chunk> findOne(Long id) {
        log.debug("Request to get Chunk : {}", id);
        return chunkRepository.findById(id);
    }

    /**
     * Delete the chunk by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Chunk : {}", id);
        chunkRepository.deleteById(id);
    }
}
