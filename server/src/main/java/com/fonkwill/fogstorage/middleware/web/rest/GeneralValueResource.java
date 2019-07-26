package com.fonkwill.fogstorage.middleware.web.rest;

import com.fonkwill.fogstorage.middleware.domain.GeneralValue;
import com.fonkwill.fogstorage.middleware.service.GeneralValueService;
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
 * REST controller for managing {@link com.fonkwill.fogstorage.middleware.domain.GeneralValue}.
 */
@RestController
@RequestMapping("/api")
public class GeneralValueResource {

    private final Logger log = LoggerFactory.getLogger(GeneralValueResource.class);

    private static final String ENTITY_NAME = "middlewareGeneralValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GeneralValueService generalValueService;

    public GeneralValueResource(GeneralValueService generalValueService) {
        this.generalValueService = generalValueService;
    }

    /**
     * {@code POST  /general-values} : Create a new generalValue.
     *
     * @param generalValue the generalValue to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new generalValue, or with status {@code 400 (Bad Request)} if the generalValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/general-values")
    public ResponseEntity<GeneralValue> createGeneralValue(@RequestBody GeneralValue generalValue) throws URISyntaxException {
        log.debug("REST request to save GeneralValue : {}", generalValue);
        if (generalValue.getId() != null) {
            throw new BadRequestAlertException("A new generalValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GeneralValue result = generalValueService.save(generalValue);
        return ResponseEntity.created(new URI("/api/general-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /general-values} : Updates an existing generalValue.
     *
     * @param generalValue the generalValue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated generalValue,
     * or with status {@code 400 (Bad Request)} if the generalValue is not valid,
     * or with status {@code 500 (Internal Server Error)} if the generalValue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/general-values")
    public ResponseEntity<GeneralValue> updateGeneralValue(@RequestBody GeneralValue generalValue) throws URISyntaxException {
        log.debug("REST request to update GeneralValue : {}", generalValue);
        if (generalValue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        GeneralValue result = generalValueService.save(generalValue);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, generalValue.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /general-values} : get all the generalValues.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of generalValues in body.
     */
    @GetMapping("/general-values")
    public List<GeneralValue> getAllGeneralValues() {
        log.debug("REST request to get all GeneralValues");
        return generalValueService.findAll();
    }

    /**
     * {@code GET  /general-values/:id} : get the "id" generalValue.
     *
     * @param id the id of the generalValue to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the generalValue, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/general-values/{id}")
    public ResponseEntity<GeneralValue> getGeneralValue(@PathVariable Long id) {
        log.debug("REST request to get GeneralValue : {}", id);
        Optional<GeneralValue> generalValue = generalValueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(generalValue);
    }

    /**
     * {@code DELETE  /general-values/:id} : delete the "id" generalValue.
     *
     * @param id the id of the generalValue to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/general-values/{id}")
    public ResponseEntity<Void> deleteGeneralValue(@PathVariable Long id) {
        log.debug("REST request to delete GeneralValue : {}", id);
        generalValueService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
