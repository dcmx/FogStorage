package com.fonkwill.fogstorage.middleware.shared.service.impl;

import com.fonkwill.fogstorage.middleware.shared.service.NodeService;
import com.fonkwill.fogstorage.middleware.shared.domain.Node;
import com.fonkwill.fogstorage.middleware.shared.repository.NodeRepository;
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
     * Get one node by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Node> findOne(Long id) {
        log.debug("Request to get Node : {}", id);
        return nodeRepository.findById(id);
    }

    /**
     * Delete the node by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Node : {}", id);
        nodeRepository.deleteById(id);
    }
}
