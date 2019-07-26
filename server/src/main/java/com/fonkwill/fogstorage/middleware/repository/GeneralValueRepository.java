package com.fonkwill.fogstorage.middleware.repository;

import com.fonkwill.fogstorage.middleware.domain.GeneralValue;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the GeneralValue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GeneralValueRepository extends JpaRepository<GeneralValue, Long> {

}
