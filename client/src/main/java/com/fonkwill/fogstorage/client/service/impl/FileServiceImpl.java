package com.fonkwill.fogstorage.client.service.impl;

import com.fonkwill.fogstorage.client.client.FogStorageService;
import com.fonkwill.fogstorage.client.client.FogStorageServiceProvider;
import com.fonkwill.fogstorage.client.domain.*;
import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;
import com.fonkwill.fogstorage.client.encryption.impl.AesDecryptionService;
import com.fonkwill.fogstorage.client.encryption.impl.AesEncryptionService;
import com.fonkwill.fogstorage.client.repository.PlacementRepository;
import com.fonkwill.fogstorage.client.service.FileService;
import com.fonkwill.fogstorage.client.service.FogStorageContext;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);


    private PlacementRepository placementRepository;

    private FogStorageContext fogStorageContext;

    protected FogStorageService fogStorageService;

    private FogStorageServiceProvider fogStorageServiceProvider;

    private FileDownloadService fileDownloadService;

    private FileUploadService fileUploadService;

    private Retrofit retrofit;

    public FileServiceImpl(PlacementRepository placementRepository, FogStorageContext fogStorageContext, FogStorageServiceProvider fogStorageServiceProvider)  {
        this.placementRepository = placementRepository;
        this.fogStorageContext = fogStorageContext;
        this.fogStorageServiceProvider = fogStorageServiceProvider;
    }


    @Override
    public MeasurementResult download(Path toPlacement) throws FileServiceException {
        fileDownloadService = new FileDownloadService(fogStorageServiceProvider, fogStorageContext.getHosts());

        RegenerationInfo placement = placementRepository.getPlacement(toPlacement);
        List<Placement> placementList = placement.getPlacementList();

        if (placement.getKey() != null && !placement.getKey().isEmpty()) {
            try {
                AesDecryptionService encryptionService = new AesDecryptionService(placement.getKey());
                fileDownloadService.enableEncryption();
                fileDownloadService.setDecrypter(encryptionService);
            } catch (EncryptionException e) {
                throw new FileServiceException("Error at creating Encryption Service");
            }
        } else {
            fileDownloadService.disableEncryption();
        }


        Path targetDirectory = toPlacement.getParent();
        Path targetFilePath = Paths.get(targetDirectory.toString(), placement.getFileName());

        if (placementList.size() == 1) {
            return fileDownloadService.downloadAsOne(placement, targetFilePath);
        } else {
            return fileDownloadService.downloadInParts(placement, placementList, targetFilePath);
        }

    }


    @Override
    public MeasurementResult upload(Path toFile, UploadMode uploadMode) throws FileServiceException {
        fileUploadService = new FileUploadService(fogStorageServiceProvider, fogStorageContext.getHosts());

        RegenerationInfo regenerationInfo = new RegenerationInfo();

        if (fogStorageContext.isEncryptionMode()) {
            try {
                AesEncryptionService encryptionService = new AesEncryptionService();
                regenerationInfo.setKey(encryptionService.getKey());
                fileUploadService.enableEncryption();
                fileUploadService.setEncrypter(encryptionService);
            } catch (EncryptionException e) {
                throw new FileServiceException("Error at creating Encryption Service", e);
            }
        } else {
            fileUploadService.disableEncryption();
        }

        int bytesForSplit = -1;

        File originalFile = toFile.toFile();
        String fileName = originalFile.getName();

        List<ProcessingResult> processingResults;
        if (fogStorageContext.isSplitMode()) {
            bytesForSplit = fogStorageContext.getCountBytesForSplit();
            processingResults = fileUploadService.uploadInParts(toFile, uploadMode, bytesForSplit);

        } else {
            processingResults =  fileUploadService.uploadAsOne(toFile, uploadMode);
        }

        List<Placement> placementList = new ArrayList<>();

        MeasurementResult measurementResult = new MeasurementResult();
        for (ProcessingResult processingResult : processingResults) {
            Placement placement = processingResult.getPlacement();
            placementList.add(placement);
            measurementResult.addMeasurement(processingResult.getMeasurement());
        }

        regenerationInfo.setPlacementList(placementList);
        regenerationInfo.setFileName(fileName);
        regenerationInfo.setFileSize(originalFile.length());

        if (bytesForSplit > -1) {
            regenerationInfo.setBytesOfPart(bytesForSplit);
        }

        Path targetDirectory = toFile.getParent();
        Path targetPlacementFilePath = Paths.get(targetDirectory.toString(), fileName +".fog" );

        placementRepository.savePlacement(targetPlacementFilePath,  regenerationInfo);

        return measurementResult;

    }






}
