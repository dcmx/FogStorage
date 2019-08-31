package com.fonkwill.fogstorage.client.service.impl;

import com.fonkwill.fogstorage.client.client.FogStorageFileService;
import com.fonkwill.fogstorage.client.client.FogStorageServiceHost;
import com.fonkwill.fogstorage.client.client.FogStorageServiceProvider;
import com.fonkwill.fogstorage.client.client.vm.PlacementVM;
import com.fonkwill.fogstorage.client.domain.*;
import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;
import com.fonkwill.fogstorage.client.security.EnDeCryptionService;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import com.fonkwill.fogstorage.client.service.utils.Stopwatch;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import retrofit2.Call;
import retrofit2.Response;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


public class FileUploadService extends  AbstractFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    private Map<Integer, ProcessingResult> processingResultMap = new ConcurrentHashMap<>();


    public FileUploadService(FogStorageServiceProvider fogStorageServiceProvider, EnDeCryptionService enDeCryptionService, TaskExecutor taskExecutor, int threadsPerService) {
        super(fogStorageServiceProvider, taskExecutor, enDeCryptionService, threadsPerService);

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

        Phaser phaser = new Phaser(1);
        try (InputStream is =  new FileInputStream(originalFile)) {
            boolean stop = false;
            int i = 0;
            while (!stop) {
                int bytesRead = is.read(content, 0, content.length);
                if (bytesRead == -1) {
                    break;
                }
                if  (bytesRead < bytesForSplit) {
                    byte[] endContent = Arrays.copyOf(content, bytesRead);
                    content = endContent;
                    stop = true;
                }

                transferThreads.put(i);
                //one execution
                phaser.register();
                processingList.add(null);
                UploadTask uploadTask = new UploadTask(content, uploadMode,phaser, i);
                taskExecutor.execute(uploadTask);
                i++;
              //  ProcessingResult processingResult = upload(content, uploadMode);
               // processingList.add(processingResult);

            }
        } catch (FileNotFoundException e) {
            throw new FileServiceException("File could not be found", e);
        } catch (IOException e) {
            throw new FileServiceException("File could not be handled", e);
        } catch (InterruptedException e) {
            logger.error("Upload was interrupted", e);
        }

        phaser.arriveAndAwaitAdvance();

        for (Map.Entry<Integer, ProcessingResult> entry : processingResultMap.entrySet()) {
           processingList.set(entry.getKey(), entry.getValue());
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

        FogStorageServiceHost fogStorageServiceHost = fogStorageServiceProvider.getService();

        Placement placement = null;
        Measurement measurement = null;

        boolean successful = false;
        Long throughFogNodeTotalTime = null;
        while (fogStorageServiceHost!=null && !successful){
            FogStorageFileService fogStorageFileService = fogStorageServiceHost.getFogStorageFileService();
            Call<PlacementVM> uploadCall = fogStorageFileService.upload(requestBody, placementStrategy.isUseFogAsStorage(), placementStrategy.getDataChunksCount(), placementStrategy.getParityChunksCount());

            Response<PlacementVM> response = null;
            try {
                Stopwatch stopwatch = new Stopwatch();
                response = uploadCall.execute();
                throughFogNodeTotalTime = stopwatch.stop();
            } catch (IOException e) {
                logger.error("Could not execute upload call successfully.", e);
                fogStorageServiceProvider.markAsInvalid(fogStorageServiceHost);
                fogStorageServiceHost = fogStorageServiceProvider.getService();
                continue;
            }
            if (!response.isSuccessful()) {
                logger.error("Got error at uploading, error-code {}", response.code());
                fogStorageServiceProvider.markAsInvalid(fogStorageServiceHost);
                fogStorageServiceHost = fogStorageServiceProvider.getService();
                continue;
            }
            PlacementVM responseVM = response.body();
            try {
                if (responseVM != null) {
                    placement =  enDeCryptionService.decryptPlacement(responseVM.getEncodedPlacement(), fogStorageServiceHost.getSharedSecret().getValue());
                }
                else {
                    logger.error("Placement could not retrieveed");
                    fogStorageServiceProvider.markAsInvalid(fogStorageServiceHost);
                    fogStorageServiceHost = fogStorageServiceProvider.getService();
                    continue;
                }
            } catch (EncryptionException e) {
                logger.error("Could not decrpyt placement");
                fogStorageServiceProvider.markAsInvalid(fogStorageServiceHost);
                fogStorageServiceHost = fogStorageServiceProvider.getService();
                continue;
            }


            successful = true;


            measurement = new Measurement(response.headers());
        }

        if (!successful) {
            throw new FileServiceException("Could not upload part correctly");
        }

        measurement.setEnDecryptionTime(encryptionTime);
        measurement.setThroughFogNodeTotalTime(fogStorageServiceHost.getHost(), throughFogNodeTotalTime);

        ProcessingResult result = new ProcessingResult();
        result.setMeasurement(measurement);
        result.setPlacement(placement);

        return result;

    }

    private class UploadTask implements Runnable {

        private final int index;

        private byte[] content;

        private UploadMode uploadMode;

        private Phaser phaser;


        public UploadTask(byte[] content, UploadMode uploadMode, Phaser phaser, int index) {
            this.content = Arrays.copyOf(content, content.length);
            this.uploadMode = uploadMode;
            this.phaser = phaser;
            this.index = index;
        }

        @Override
        public void run() {
            try {
              ProcessingResult processingResult =  upload(content, uploadMode);
              processingResultMap.put(index, processingResult);
            } catch (FileServiceException e) {
                logger.error("Could not upload File", e);
            }
            transferThreads.poll();
            this.phaser.arriveAndDeregister();
        }
    }


}
