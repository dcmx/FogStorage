package com.fonkwill.fogstorage.middleware.api;

import com.fonkwill.fogstorage.middleware.shared.domain.Node;
import com.fonkwill.fogstorage.middleware.controller.service.NodeService;
import com.fonkwill.fogstorage.middleware.api.errors.BadRequestAlertException;

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
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * REST controller for managing {@link com.fonkwill.fogstorage.middleware.shared.domain.Node}.
 */
@RestController
@RequestMapping("/api")
public class NodeResource {

    private final Logger log = LoggerFactory.getLogger(NodeResource.class);

    private static final String ENTITY_NAME = "middlewareNode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NodeService nodeService;

    public NodeResource(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * {@code POST  /nodes} : Create a new node.
     *
     * @param node the node to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new node, or with status {@code 400 (Bad Request)} if the node has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/nodes")
    public ResponseEntity<Node> createNode(@RequestBody Node node) throws URISyntaxException {
        log.debug("REST request to save Node : {}", node);
        if (existsAlready(node)) {
            throw new BadRequestAlertException("The node is already stored - use update", ENTITY_NAME, "idexists");
        }
        Node result = nodeService.save(node);
        return ResponseEntity.created(new URI("/api/nodes/" + result.getName()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getName().toString()))
            .body(result);
    }

    private boolean existsAlready(@RequestBody Node node) {
        boolean alreadyExists = false;
        try {
            nodeService.findOne(node.getName()).get();
            alreadyExists = true;
        }catch(NoSuchElementException ex) {
            //do nothing, as boolean is set already
        }
        return alreadyExists;
    }

    /**
     * {@code PUT  /nodes} : Updates an existing node.
     *
     * @param node the node to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated node,
     * or with status {@code 400 (Bad Request)} if the node is not valid,
     * or with status {@code 500 (Internal Server Error)} if the node couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/nodes")
    public ResponseEntity<Node> updateNode(@RequestBody Node node) throws URISyntaxException {
        log.debug("REST request to update Node : {}", node);
        if (node.getName() == null) {
            throw new BadRequestAlertException("Invalid name", ENTITY_NAME, "idnull");
        }
        if (!existsAlready(node)) {
            throw new BadRequestAlertException("Node doesn't exist", ENTITY_NAME, "idnull");
        }
        Node result = nodeService.save(node);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, node.getName()))
            .body(result);
    }

    /**
     * {@code GET  /nodes} : get all the nodes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of nodes in body.
     */
    @GetMapping("/nodes")
    public List<Node> getAllNodes() {
        log.debug("REST request to get all Nodes");
        return nodeService.findAll();
    }

    /**
     * {@code GET  /nodes/:name} : get the "name" node.
     *
     * @param name the name of the node to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the node, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/nodes/{name}")
    public ResponseEntity<Node> getNode(@PathVariable String name) {
        log.debug("REST request to get Node : {}", name);
        Optional<Node> node = nodeService.findOne(name);
        return ResponseUtil.wrapOrNotFound(node);
    }

    /**
     * {@code DELETE  /nodes/:id} : delete the "name" node.
     *
     * @param name the name of the node to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/nodes/{name}")
    public ResponseEntity<Void> deleteNode(@PathVariable String name) {
        log.debug("REST request to delete Node : {}", name);
        nodeService.delete(name);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, name)).build();
    }
}
