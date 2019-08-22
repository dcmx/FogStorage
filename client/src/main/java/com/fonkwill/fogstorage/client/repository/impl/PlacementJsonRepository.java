package com.fonkwill.fogstorage.client.repository.impl;

import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.domain.RegenerationInfo;
import com.fonkwill.fogstorage.client.repository.PlacementRepository;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import com.fonkwill.fogstorage.client.service.impl.FileUploadService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Component
public class PlacementJsonRepository implements PlacementRepository {

    private static final Logger logger = LoggerFactory.getLogger(PlacementJsonRepository.class);

    public RegenerationInfo getPlacement(Path toPlacement) throws FileServiceException {
        if (toPlacement == null) {
            throw new IllegalArgumentException("No path for placement available");
        }
        RegenerationInfo placement = null;
        Gson gson = new Gson();
        try (FileReader fileReader = new FileReader(toPlacement.toFile())){
             placement = gson.fromJson(fileReader, RegenerationInfo.class);
        } catch (IOException e) {
            throw new FileServiceException("Could not read in placement file!");
        }
        return placement;
    }


    public void savePlacement(Path placementPath, RegenerationInfo placement) throws  FileServiceException {
        if (placementPath == null || placement == null) {
            throw new IllegalArgumentException("Missing arguments to store placement");
        }
        Gson gson = new Gson();

        try(FileWriter fileWriter = new FileWriter(placementPath.toFile())) {

            gson.toJson(placement, fileWriter);
        } catch (IOException e) {
            throw new FileServiceException("Could not write placement-file to path: " + placementPath);
        }

    }
}
