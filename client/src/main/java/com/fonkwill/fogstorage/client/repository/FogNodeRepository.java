package com.fonkwill.fogstorage.client.repository;

import com.fonkwill.fogstorage.client.domain.FogNode;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
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
