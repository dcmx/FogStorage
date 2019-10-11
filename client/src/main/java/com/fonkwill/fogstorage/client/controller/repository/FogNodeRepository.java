package com.fonkwill.fogstorage.client.controller.repository;

import com.fonkwill.fogstorage.client.shared.domain.FogNode;
import com.fonkwill.fogstorage.client.fileservice.exception.FileServiceException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public interface FogNodeRepository {

    /**
     * Gets all public information for all nodes
     *
     * @return A set of node public information
     */
    Set<FogNode> getAll() throws FileServiceException;


}
