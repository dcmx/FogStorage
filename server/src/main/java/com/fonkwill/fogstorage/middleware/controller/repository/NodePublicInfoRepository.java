package com.fonkwill.fogstorage.middleware.controller.repository;

import com.fonkwill.fogstorage.middleware.controller.domain.NodePublicInfo;

import java.util.Set;

public interface NodePublicInfoRepository {

    Set<NodePublicInfo> getAll();


}
