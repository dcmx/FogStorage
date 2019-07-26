package com.fonkwill.fogstorage.middleware.service.impl;

import com.fonkwill.fogstorage.middleware.service.GeneralValueService;
import com.fonkwill.fogstorage.middleware.domain.GeneralValue;
import com.fonkwill.fogstorage.middleware.repository.GeneralValueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link GeneralValue}.
 */
@Service
@Transactional
public class GeneralValueServiceImpl implements GeneralValueService {

    private final Logger log = LoggerFactory.getLogger(GeneralValueServiceImpl.class);

    private final GeneralValueRepository generalValueRepository;

    public GeneralValueServiceImpl(GeneralValueRepository generalValueRepository) {
        this.generalValueRepository = generalValueRepository;
    }

    /**
     * Save a generalValue.
     *
     * @param generalValue the entity to save.
     * @return the persisted entity.
     */
    @Override
    public GeneralValue save(GeneralValue generalValue) {
        log.debug("Request to save GeneralValue : {}", generalValue);
        return generalValueRepository.save(generalValue);
    }

    /**
     * Get all the generalValues.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GeneralValue> findAll() {
        log.debug("Request to get all GeneralValues");
        return generalValueRepository.findAll();
    }


    /**
     * Get one generalValue by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<GeneralValue> findOne(Long id) {
        log.debug("Request to get GeneralValue : {}", id);
        return generalValueRepository.findById(id);
    }

    /**
     * Delete the generalValue by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete GeneralValue : {}", id);
        generalValueRepository.deleteById(id);
    }
}
