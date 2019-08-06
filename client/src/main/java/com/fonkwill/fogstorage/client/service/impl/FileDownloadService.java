package com.fonkwill.fogstorage.client.service.impl;

import com.fonkwill.fogstorage.client.client.FogStorageService;
import com.fonkwill.fogstorage.client.domain.Measurement;
import com.fonkwill.fogstorage.client.domain.MeasurementResult;
import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.domain.RegenerationInfo;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class FileDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

    private FogStorageService fogStorageService;

    public FileDownloadService(FogStorageService fogStorageService) {
        this.fogStorageService = fogStorageService;
    }

    public Measurement download(ByteBuffer content, Placement placement) throws FileServiceException {

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

        try {
            content.put(responseBody.bytes());
        } catch (IOException e) {
            throw new FileServiceException("Could not get byte array");
        }

        return new Measurement(response.headers());
    }


    public MeasurementResult downloadAsOne(RegenerationInfo regenerationInfo, Path targetFilePath) throws FileServiceException {
        Long bufferSize =  regenerationInfo.getFileSize();
        if (bufferSize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Could not download file, at it is too big");
        }

        ByteBuffer buffer = ByteBuffer.wrap(new byte[bufferSize.intValue()]);

        Measurement measurement = download(buffer, regenerationInfo.getPlacementList().get(0));

        if (Files.exists(targetFilePath)){
            logger.warn("File exists, will be overriden");
        }
        try {
            Files.write(targetFilePath, buffer.array());
        } catch (IOException e) {
            throw new FileServiceException("Could not write file to disk");
        }
        MeasurementResult measurementResult = new MeasurementResult();
        measurementResult.addMeasurement(measurement);

        return measurementResult;
    }

    public MeasurementResult  downloadInParts(RegenerationInfo placement, List<Placement> placementList, Path targetFilePath) throws FileServiceException {
        int bytes = placement.getBytesOfPart();
        if (bytes < 1) {
            throw new IllegalArgumentException("Could not download file with missing information of bytes for part");
        }

        try {
            Files.deleteIfExists(targetFilePath);
        } catch (IOException e) {
            throw new FileServiceException("Could not delete File");
        }

        MeasurementResult measurementResult = new MeasurementResult();

        byte[] content = new byte[bytes];
        ByteBuffer buffer = ByteBuffer.wrap(content);

        for (Placement placementForDownload : placementList) {

            Measurement measurement = download(buffer, placementForDownload);

            content = buffer.array();
            buffer.clear();

            if (placementForDownload.getFileInfo().getSize() < bytes) {
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
