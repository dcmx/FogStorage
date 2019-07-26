package com.fonkwill.fogstorage.middleware.shared.service;

import com.fonkwill.fogstorage.middleware.shared.domain.Node;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Node}.
 */
public interface NodeService {

    /**
     * Save a node.
     *
     * @param node the entity to save.
     * @return the persisted entity.
     */
    Node save(Node node);

    /**
     * Get all the nodes.
     *
     * @return the list of entities.
     */
    List<Node> findAll();


    /**
     * Get the "id" node.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Node> findOne(Long id);

    /**
     * Delete the "id" node.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
