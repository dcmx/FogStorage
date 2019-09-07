package com.fonkwill.fogstorage.client.service;

import com.fonkwill.fogstorage.client.domain.Measurement;
import com.fonkwill.fogstorage.client.domain.MeasurementResult;
import com.fonkwill.fogstorage.client.domain.UploadMode;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;


public interface FileService {

    /**
     * Downloads a file from the FogStorage according to the given placement-file and stores it next to
     * the placement-file on the disk.
     * @param toPlacement A path to a file with the placement information from the uplaod
     * @return The measurement of the different processing steps for the upload
     * @throws FileServiceException If the download is not possible
     */
    MeasurementResult download(Path toPlacement) throws FileServiceException;

    /**
     * Uploadas a file to the FogStorage respecting the given upload mode and stores the placement-file net
     * to the file on the disk.
     * @param toFile A path to a file which should be uploaded
     * @param uploadMode The settings for uploading the file
     * @return The measurement of the different processing steps for the upload
     * @throws FileServiceException If the upload is not possible
     */
    MeasurementResult upload(Path toFile, UploadMode uploadMode) throws FileServiceException;

}
