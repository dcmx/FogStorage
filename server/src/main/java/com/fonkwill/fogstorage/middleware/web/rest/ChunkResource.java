package com.fonkwill.fogstorage.middleware.web.rest;

import com.fonkwill.fogstorage.middleware.domain.Chunk;
import com.fonkwill.fogstorage.middleware.service.ChunkService;
import com.fonkwill.fogstorage.middleware.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.fonkwill.fogstorage.middleware.domain.Chunk}.
 */
@RestController
@RequestMapping("/api")
public class ChunkResource {

    private final Logger log = LoggerFactory.getLogger(ChunkResource.class);

    private static final String ENTITY_NAME = "middlewareChunk";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChunkService chunkService;

    public ChunkResource(ChunkService chunkService) {
        this.chunkService = chunkService;
    }

    /**
     * {@code POST  /chunks} : Create a new chunk.
     *
     * @param chunk the chunk to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chunk, or with status {@code 400 (Bad Request)} if the chunk has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/chunks")
    public ResponseEntity<Chunk> createChunk(@RequestBody Chunk chunk) throws URISyntaxException {
        log.debug("REST request to save Chunk : {}", chunk);
        if (chunk.getId() != null) {
            throw new BadRequestAlertException("A new chunk cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Chunk result = chunkService.save(chunk);
        return ResponseEntity.created(new URI("/api/chunks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /chunks} : Updates an existing chunk.
     *
     * @param chunk the chunk to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chunk,
     * or with status {@code 400 (Bad Request)} if the chunk is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chunk couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/chunks")
    public ResponseEntity<Chunk> updateChunk(@RequestBody Chunk chunk) throws URISyntaxException {
        log.debug("REST request to update Chunk : {}", chunk);
        if (chunk.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Chunk result = chunkService.save(chunk);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, chunk.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /chunks} : get all the chunks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chunks in body.
     */
    @GetMapping("/chunks")
    public List<Chunk> getAllChunks() {
        log.debug("REST request to get all Chunks");
        return chunkService.findAll();
    }

    /**
     * {@code GET  /chunks/:id} : get the "id" chunk.
     *
     * @param id the id of the chunk to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chunk, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/chunks/{id}")
    public ResponseEntity<Chunk> getChunk(@PathVariable Long id) {
        log.debug("REST request to get Chunk : {}", id);
        Optional<Chunk> chunk = chunkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chunk);
    }

    /**
     * {@code DELETE  /chunks/:id} : delete the "id" chunk.
     *
     * @param id the id of the chunk to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/chunks/{id}")
    public ResponseEntity<Void> deleteChunk(@PathVariable Long id) {
        log.debug("REST request to delete Chunk : {}", id);
        chunkService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
