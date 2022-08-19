package com.fonkwill.fogstorage.middleware.controller.repository;

import com.fonkwill.fogstorage.middleware.shared.domain.Node;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Node entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NodeRepository extends JpaRepository<Node, String> {

}
