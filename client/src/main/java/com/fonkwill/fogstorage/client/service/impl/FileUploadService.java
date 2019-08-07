package com.fonkwill.fogstorage.client.service.impl;

import com.fonkwill.fogstorage.client.client.FogStorageService;
import com.fonkwill.fogstorage.client.domain.*;
import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import com.fonkwill.fogstorage.client.service.utils.Stopwatch;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileUploadService extends  AbstractFileService {


    private FogStorageService fogStorageService;

    public FileUploadService(FogStorageService fogStorageService) {
        this.fogStorageService = fogStorageService;
    }

    public List<ProcessingResult> uploadAsOne(Path toFile, UploadMode uploadMode) throws FileServiceException {
        byte[] content;
        try {
            content =  Files.readAllBytes(toFile);
        } catch (IOException e) {
            throw new FileServiceException("Could not read in File", e);
        }

        ProcessingResult processingResult = upload(content, uploadMode);

        List<ProcessingResult> processingResults  = new ArrayList<>();
        processingResults.add(processingResult);
        return processingResults;
    }

    public List<ProcessingResult> uploadInParts(Path toFile, UploadMode uploadMode, int bytesForSplit) throws FileServiceException {

        if (bytesForSplit < 1) {
            throw new IllegalArgumentException("The given number of bytes for the file splitting is not valid");
        }

        File originalFile = toFile.toFile();

        List<ProcessingResult> processingList = new ArrayList<>();

        ProcessingResult uploadResult;

        byte[] content = new byte[bytesForSplit];

        try (InputStream is =  new FileInputStream(originalFile)) {
            boolean stop = false;
            while (!stop) {
                int bytesRead = is.read(content, 0, content.length);
                if  (bytesRead < bytesForSplit) {
                    byte[] endContent = Arrays.copyOf(content, bytesRead);
                    content = endContent;
                    stop = true;
                }
                uploadResult = upload(content, uploadMode);
                processingList.add(uploadResult);
            }
        } catch (FileNotFoundException e) {
            throw new FileServiceException("File could not be found", e);
        } catch (IOException e) {
            throw new FileServiceException("File could not be handled", e);
        }

        return processingList;

    }


    public ProcessingResult upload(byte[] content, UploadMode uploadMode) throws FileServiceException {
        long encryptionTime = 0L;
        if (encryptionActivated) {
            try {
                Stopwatch stopwatch = new Stopwatch();
                content = encrypter.encrypt(content);
                encryptionTime = stopwatch.stop();
            } catch (EncryptionException e) {
                throw new FileServiceException("Could not encrypt data");
            }
        }

        PlacementStrategy placementStrategy = uploadMode.getPlacementStrategy();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), content);
        MultipartBody.Part requestBody = MultipartBody.Part.createFormData("uploadFile", "uploadFile", requestFile);

        Call<Placement> uploadCall = fogStorageService.upload(requestBody, placementStrategy.isUseFogAsStorage(), placementStrategy.getDataChunksCount(), placementStrategy.getParityChunksCount());

        Response<Placement> response = null;
        try {
            response = uploadCall.execute();
        } catch (IOException e) {
            throw new FileServiceException("Could not execute upload call successfully.", e);
        }
        if (!response.isSuccessful()) {
            String error = "Got error at uploading, error-code" + response.code();
            throw new FileServiceException(error );
        }

        Placement placement = response.body();
        Measurement measurement = new Measurement(response.headers());
        measurement.setEnDecryptionTime(encryptionTime);

        ProcessingResult result = new ProcessingResult();
        result.setMeasurement(measurement);
        result.setPlacement(placement);

        return result;

    }


}
