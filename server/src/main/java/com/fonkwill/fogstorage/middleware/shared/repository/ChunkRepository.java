package com.fonkwill.fogstorage.middleware.shared.repository;

import com.fonkwill.fogstorage.middleware.shared.domain.Chunk;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Chunk entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {

}
