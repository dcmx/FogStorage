package com.fonkwill.fogstorage.client.fileservice.service.impl;

import com.fonkwill.fogstorage.client.transferhandler.FogStorageServiceHost;
import com.fonkwill.fogstorage.client.transferhandler.FogStorageServiceProvider;
import com.fonkwill.fogstorage.client.transferhandler.vm.PlacementVM;
import com.fonkwill.fogstorage.client.shared.domain.*;
import com.fonkwill.fogstorage.client.cryptoservice.exception.EncryptionException;
import com.fonkwill.fogstorage.client.cryptoservice.CryptoService;
import com.fonkwill.fogstorage.client.fileservice.exception.FileServiceException;
import com.fonkwill.fogstorage.client.fileservice.service.utils.Stopwatch;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;

public class FileDownloadService extends  AbstractFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

    private Map<Integer, Measurement> measurementResultMap = new ConcurrentHashMap<>();

    private LinkedBlockingQueue<IndexedBytes> indexedBytesQueue = new LinkedBlockingQueue<>();

    private int bytesToSplit;

    private int numberOfParts;

    private Path targetFilePath;

    public FileDownloadService(FogStorageServiceProvider fogStorageServiceProvider, TaskExecutor taskExecutor, CryptoService enDeCryptionService, int threadsPerService) {
        super(fogStorageServiceProvider, taskExecutor, enDeCryptionService, threadsPerService);
    }


    public Measurement download(Bytes bytes, Placement placement) throws FileServiceException {

        FogStorageServiceHost fogStorageService = fogStorageServiceProvider.getServiceForPlacement(placement);;

        Measurement measurement = null;

        boolean successful = false;
        byte[] contentDownload = null;
        Long uploadThroughFogNodeTotal = null;
        while (fogStorageService != null && !successful) {
            SharedSecret sharedSecret = fogStorageService.getSharedSecret();

            PlacementVM placementVM = new PlacementVM();
            try {
                String encryptedPlacement = enDeCryptionService.encryptPlacement(placement, sharedSecret.getValue());
                placementVM.setEncodedPlacement(encryptedPlacement);
            } catch (EncryptionException e) {
               fogStorageServiceProvider.markAsInvalid(fogStorageService);
               fogStorageService = fogStorageServiceProvider.getService();
               logger.error("Could not encrypt placement");
               continue;
            }
            Call<ResponseBody> downloadCall = fogStorageService.getFogStorageFileService().download(placementVM);

            Response<ResponseBody> response = null;
            try {
                Stopwatch stopwatch = new Stopwatch();
                response = downloadCall.execute();
                uploadThroughFogNodeTotal = stopwatch.stop();
            } catch (IOException e) {
                logger.error("Could not execute download call successfully.", e);
                fogStorageServiceProvider.markAsInvalid(fogStorageService);
                fogStorageService = fogStorageServiceProvider.getService();
                continue;
            }
            if (!response.isSuccessful()) {
                logger.error("Got error at downloading, error-code {} " , response.code());
                fogStorageServiceProvider.markAsInvalid(fogStorageService);
                fogStorageService = fogStorageServiceProvider.getService();
                continue;
            }

            ResponseBody responseBody = response.body();
            measurement = new Measurement(response.headers());
            try {
                contentDownload = responseBody.bytes();
            } catch (IOException e) {
                logger.error("could not get bytes from response");
                fogStorageServiceProvider.markAsInvalid(fogStorageService);
                fogStorageService = fogStorageServiceProvider.getService();
                continue;
            }
            successful = true;
        }

        if (!successful) {
            throw new FileServiceException("Could not download part correctly");
        }


        Long decryptionTime = 0L;

        if (encryptionActivated) {
            Stopwatch stopWatch = new Stopwatch();
            try {
                contentDownload = decrypter.decrypt(contentDownload);
            } catch (EncryptionException e) {
                throw new FileServiceException("Could not decrypt data", e);
            }
            decryptionTime = stopWatch.stop();
        }

        bytes.setContent(contentDownload);

        measurement.setEnDecryptionTime(decryptionTime);
        measurement.setThroughFogNodeTotalTime(fogStorageService.getHost(), uploadThroughFogNodeTotal);
        return measurement;
    }


    public MeasurementResult downloadAsOne(RegenerationInfo regenerationInfo, Path targetFilePath) throws FileServiceException {
        this.targetFilePath = targetFilePath;
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
        this.targetFilePath = targetFilePath;
        this.bytesToSplit = placement.getBytesOfPart();
        if (bytesToSplit < 1) {
            throw new IllegalArgumentException("Could not download file with missing information of bytes for part");
        }

        try {
            Files.deleteIfExists(targetFilePath);
        } catch (IOException e) {
            throw new FileServiceException("Could not delete File");
        }

        MeasurementResult measurementResult = new MeasurementResult();


        Phaser phaser = new Phaser(1);
        int i = 0;

        numberOfParts = placementList.size();

        FileWriterTask fileWriterTask = new FileWriterTask();
        taskExecutor.execute(fileWriterTask);

        for (Placement placementForDownload : placementList) {

            try {
                transferThreads.put(i);
                phaser.register();
                DownloadTask downloadTask = new DownloadTask(placementForDownload, phaser, i);
                taskExecutor.execute(downloadTask);
                i++;
            } catch (InterruptedException e) {
                logger.error("Upload was interrupted", e);
            }



            //measurementResult.addMeasurement(measurement);
        }
        phaser.arriveAndAwaitAdvance();

        for (Map.Entry<Integer, Measurement> entry : measurementResultMap.entrySet()) {
            measurementResult.addMeasurement(entry.getValue());
        }
        return measurementResult;
    }


    public class DownloadTask implements Runnable{

        private Bytes bytes;

        private Placement placement;

        private Phaser phaser;

        private int index;

        public DownloadTask(Placement placementForDownload, Phaser phaser, int i) {
            this.bytes = new Bytes();
            this.placement = placementForDownload;
            this.phaser = phaser;
            this.index = i;
        }

        @Override
        public void run() {
            try {
                Measurement measurement = download(bytes, placement);

                byte[] content = bytes.getContent();
                if (placement.getFileInfo().getSize() < bytesToSplit) {
                    byte[] endContent = Arrays.copyOf(content, placement.getFileInfo().getSize().intValue());
                    content = endContent;
                }
                bytes.setContent(content);

                measurementResultMap.put(index, measurement);
                indexedBytesQueue.put(new IndexedBytes(index, bytes));
            } catch (FileServiceException e) {
                logger.error("Could not download file", e);
            } catch (InterruptedException e) {
                logger.error("Interrupted Thread", e);
            }
            transferThreads.poll();
            phaser.arriveAndDeregister();
        }
    }

    private class IndexedBytes{

        private int index;

        private Bytes bytes;

        public IndexedBytes(int index, Bytes bytes) {
            this.index = index;
            this.bytes = bytes;
        }

        public int getIndex() {
            return index;
        }

        public Bytes getBytes() {
            return bytes;
        }
    }

    public class FileWriterTask implements Runnable{

        private int currentIndex = 0;

        private Map<Integer, Bytes> bytesMap = new HashMap<>();

        @Override
        public void run() {

            while (currentIndex < numberOfParts ) {
                try {
                    IndexedBytes indexedBytes = indexedBytesQueue.take();

                    if (indexedBytes.getIndex() == currentIndex) {
                        if (!Files.exists(targetFilePath)) {
                            Files.write(targetFilePath, indexedBytes.getBytes().getContent());
                        } else {
                            Files.write(targetFilePath, indexedBytes.getBytes().getContent(), StandardOpenOption.APPEND);
                        }
                        currentIndex ++;

                        //check if for new index is already data here
                        Bytes bytes = bytesMap.get(currentIndex);
                        while (bytes != null) {
                            Files.write(targetFilePath, bytes.getContent(), StandardOpenOption.APPEND);
                            currentIndex ++;
                            bytes = bytesMap.get(currentIndex);
                        }
                    } else {
                        bytesMap.put(indexedBytes.getIndex(), indexedBytes.getBytes());
                    }
                } catch (InterruptedException e) {
                    logger.error("Interrupted Thread",e);
                } catch (IOException e) {
                    logger.error("Could not write to target file");
                }

            }

        }
    }

}
