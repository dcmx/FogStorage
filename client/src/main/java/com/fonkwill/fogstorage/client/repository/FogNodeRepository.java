package com.fonkwill.fogstorage.client.repository;

import com.fonkwill.fogstorage.client.domain.NodePublicInfo;

import java.util.Set;

public interface NodePublicInfoRepository {

    /**
     * Gets all public information for all nodes
     *
     * @return A set of node public information
     */
    Set<NodePublicInfo> getAll();


}
