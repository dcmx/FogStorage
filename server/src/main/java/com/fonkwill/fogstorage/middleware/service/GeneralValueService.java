package com.fonkwill.fogstorage.middleware.service;

import com.fonkwill.fogstorage.middleware.domain.GeneralValue;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link GeneralValue}.
 */
public interface GeneralValueService {

    /**
     * Save a generalValue.
     *
     * @param generalValue the entity to save.
     * @return the persisted entity.
     */
    GeneralValue save(GeneralValue generalValue);

    /**
     * Get all the generalValues.
     *
     * @return the list of entities.
     */
    List<GeneralValue> findAll();


    /**
     * Get the "id" generalValue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GeneralValue> findOne(Long id);

    /**
     * Delete the "id" generalValue.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
