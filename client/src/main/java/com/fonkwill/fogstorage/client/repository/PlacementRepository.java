package com.fonkwill.fogstorage.client.repository;

import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.domain.RegenerationInfo;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;

import java.nio.file.Path;
import java.util.List;

public interface PlacementRepository {

    /**
     * Gets the placement from the given file
     * @param toPlacement The file containing the placement as json
     * @return The placement object
     * @throws FileServiceException if the placement-file is not available or not readable
     */
    public RegenerationInfo getPlacement(Path toPlacement) throws FileServiceException;


    /**
     * Stores the placement as json file to the given path
     * @param placementPath The path to where the file should be stored
     * @param regenerationInfo A list of placements to store in one file
     * @throws FileServiceException if the storage of the placement file is not possible
     */
    public void savePlacement(Path placementPath, RegenerationInfo regenerationInfo) throws FileServiceException;


}
