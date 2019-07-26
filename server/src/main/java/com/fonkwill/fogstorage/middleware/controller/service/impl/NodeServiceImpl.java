package com.fonkwill.fogstorage.middleware.controller.service.impl;

import com.fonkwill.fogstorage.middleware.controller.service.NodeService;
import com.fonkwill.fogstorage.middleware.shared.domain.Node;
import com.fonkwill.fogstorage.middleware.controller.repository.NodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Node}.
 */
@Service
@Transactional
public class NodeServiceImpl implements NodeService {

    private final Logger log = LoggerFactory.getLogger(NodeServiceImpl.class);

    private final NodeRepository nodeRepository;

    public NodeServiceImpl(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    /**
     * Save a node.
     *
     * @param node the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Node save(Node node) {
        log.debug("Request to save Node : {}", node);
        return nodeRepository.save(node);
    }

    /**
     * Get all the nodes.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Node> findAll() {
        log.debug("Request to get all Nodes");
        return nodeRepository.findAll();
    }


    /**
     * Get one node by name.
     *
     * @param name the name of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Node> findOne(String name) {
        log.debug("Request to get Node : {}", name);
        return nodeRepository.findById(name);
    }

    /**
     * Delete the node by name.
     *
     * @param name the name of the entity.
     */
    @Override
    public void delete(String name) {
        log.debug("Request to delete Node : {}", name);
        nodeRepository.deleteById(name);
    }

}
