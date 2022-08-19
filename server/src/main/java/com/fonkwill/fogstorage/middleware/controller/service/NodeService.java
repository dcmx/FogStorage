package com.fonkwill.fogstorage.middleware.controller.service;

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
     * Get the "name" node.
     *
     * @param name the name of the entity.
     * @return the entity.
     */
    Optional<Node> findOne(String name);

    /**
     * Delete the "name" node.
     *
     * @param name the name of the entity.
     */
    void delete(String name);

 }
