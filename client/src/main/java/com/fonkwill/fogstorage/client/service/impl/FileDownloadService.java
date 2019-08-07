package com.fonkwill.fogstorage.client.service.impl;

import com.fonkwill.fogstorage.client.client.FogStorageService;
import com.fonkwill.fogstorage.client.domain.*;
import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import retrofit2.Call;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class FileDownloadService extends  AbstractFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

    private FogStorageService fogStorageService;

    public FileDownloadService(FogStorageService fogStorageService) {
        this.fogStorageService = fogStorageService;
    }

    public Measurement download(Bytes bytes, Placement placement) throws FileServiceException {

        Call<ResponseBody> downloadCall = fogStorageService.download(placement);

        Response<ResponseBody> response;
        try {
            response = downloadCall.execute();
        } catch (IOException e) {
            throw new FileServiceException("Could not execute download call successfully.", e);
        }
        if (!response.isSuccessful()) {
            String error = "Got error at downloading, error-code " + response.code();
            throw new FileServiceException(error );
        }

        ResponseBody responseBody = response.body();

        Long decryptionTime = 0L;
        try {
            byte[] contentDownload = responseBody.bytes();

            StopWatch stopWatch = new StopWatch();
            if (encryptionActivated) {
                try {
                    contentDownload = decrypter.decrypt(contentDownload);
                } catch (EncryptionException e) {
                    throw new FileServiceException("Could not decrypt data");
                }
            }
            stopWatch.stop();
            bytes.setContent(contentDownload);


        } catch (IOException e) {
            throw new FileServiceException("Could not get byte array");
        }

        Measurement measurement = new Measurement(response.headers());
        measurement.setEnDecryptionTime(decryptionTime);
        return measurement;
    }


    public MeasurementResult downloadAsOne(RegenerationInfo regenerationInfo, Path targetFilePath) throws FileServiceException {
        //only one placement
        Placement placement = regenerationInfo.getPlacementList().get(0);

        Long bufferSize =  placement.getFileInfo().getSize();
        if (bufferSize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Could not download file, at it is too big");
        }

        Bytes bytes = new Bytes();

        Measurement measurement = download(bytes, placement );

        if (Files.exists(targetFilePath)){
            logger.warn("File exists, will be overriden");
        }
        try {
            Files.write(targetFilePath, bytes.getContent());
        } catch (IOException e) {
            throw new FileServiceException("Could not write file to disk");
        }
        MeasurementResult measurementResult = new MeasurementResult();
        measurementResult.addMeasurement(measurement);

        return measurementResult;
    }

    public MeasurementResult  downloadInParts(RegenerationInfo placement, List<Placement> placementList, Path targetFilePath) throws FileServiceException {
        int bytesToSplit = placement.getBytesOfPart();
        if (bytesToSplit < 1) {
            throw new IllegalArgumentException("Could not download file with missing information of bytes for part");
        }

        try {
            Files.deleteIfExists(targetFilePath);
        } catch (IOException e) {
            throw new FileServiceException("Could not delete File");
        }

        MeasurementResult measurementResult = new MeasurementResult();

        byte[] content;
        Bytes bytes = new Bytes();

        for (Placement placementForDownload : placementList) {

            Measurement measurement = download(bytes, placementForDownload);

            content = bytes.getContent();

            if (placementForDownload.getFileInfo().getSize() < bytesToSplit) {
                byte[] endContent = Arrays.copyOf(content, placementForDownload.getFileInfo().getSize().intValue());
                content = endContent;
            }
            try {
                if (!Files.exists(targetFilePath)) {
                    Files.write(targetFilePath, content);
                } else {
                    Files.write(targetFilePath, content, StandardOpenOption.APPEND);
                }
            }catch (IOException e){
                logger.error("Could not write to target file");
            }
            measurementResult.addMeasurement(measurement);
        }
        return measurementResult;
    }
}
