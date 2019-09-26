package com.fonkwill.fogstorage.client.service.impl;

import com.fonkwill.fogstorage.client.client.FogStorageFileService;
import com.fonkwill.fogstorage.client.client.FogStorageServiceFactory;
import com.fonkwill.fogstorage.client.client.FogStorageServiceProvider;
import com.fonkwill.fogstorage.client.domain.*;
import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;
import com.fonkwill.fogstorage.client.encryption.impl.AesDecryptionService;
import com.fonkwill.fogstorage.client.encryption.impl.AesEncryptionService;
import com.fonkwill.fogstorage.client.repository.PlacementRepository;
import com.fonkwill.fogstorage.client.security.EnDeCryptionService;
import com.fonkwill.fogstorage.client.service.FileService;
import com.fonkwill.fogstorage.client.service.FogStorageContext;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import com.fonkwill.fogstorage.client.service.utils.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Component
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);


    private PlacementRepository placementRepository;

    private FogStorageContext fogStorageContext;

    protected FogStorageFileService fogStorageFileService;

    private FogStorageServiceFactory fogStorageServiceFactory;

    private FileDownloadService fileDownloadService;

    private FileUploadService fileUploadService;

    private Retrofit retrofit;

    private TaskExecutor taskExecutor;

    private EnDeCryptionService enDeCryptionService;

    public FileServiceImpl(PlacementRepository placementRepository, FogStorageContext fogStorageContext, FogStorageServiceFactory fogStorageServiceFactory, TaskExecutor taskExecutor, EnDeCryptionService enDeCryptionService)  {
        this.placementRepository = placementRepository;
        this.fogStorageContext = fogStorageContext;
        this.fogStorageServiceFactory = fogStorageServiceFactory;
        this.taskExecutor = taskExecutor;
        this.enDeCryptionService = enDeCryptionService;
    }


    @Override
    public MeasurementResult download(Path toPlacement) throws FileServiceException {
        logger.info("Starting to download");
        Stopwatch stopwatch = new Stopwatch();

        FogStorageServiceProvider fogStorageServiceProvider = new FogStorageServiceProvider(fogStorageServiceFactory, fogStorageContext.getHosts());
        fileDownloadService = new FileDownloadService(fogStorageServiceProvider, taskExecutor, enDeCryptionService, fogStorageContext.getThreadsPerService());

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

        MeasurementResult measurementResult;
        if (placementList.size() == 1) {
            measurementResult = fileDownloadService.downloadAsOne(placement, targetFilePath);
        } else {
            measurementResult = fileDownloadService.downloadInParts(placement, placementList, targetFilePath);
        }
        measurementResult.setFileSize(placement.getFileSize());
        measurementResult.setTotalTime(stopwatch.stop());
        logger.info("Finished downloading");
        return measurementResult;

    }


    @Override
    public MeasurementResult upload(Path toFile, UploadMode uploadMode) throws FileServiceException {
        logger.info("Starting to upload");
        Stopwatch stopwatch = new Stopwatch();

        FogStorageServiceProvider fogStorageServiceProvider = new FogStorageServiceProvider(fogStorageServiceFactory, fogStorageContext.getHosts());
        fileUploadService = new FileUploadService(fogStorageServiceProvider, enDeCryptionService, taskExecutor, fogStorageContext.getThreadsPerService());

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
        measurementResult.setFileSize(originalFile.length());
        if (bytesForSplit > -1) {
            regenerationInfo.setBytesOfPart(bytesForSplit);
        }

        Path targetDirectory = toFile.getParent();
        Path targetPlacementFilePath = Paths.get(targetDirectory.toString(), fileName +".fog" );

        placementRepository.savePlacement(targetPlacementFilePath,  regenerationInfo);

        measurementResult.setTotalTime(stopwatch.stop());
        logger.info("Finished uploading");
        return measurementResult;

    }






}
