package com.fonkwill.fogstorage.client.service;

import com.fonkwill.fogstorage.client.domain.MeasurementResult;
import com.fonkwill.fogstorage.client.domain.PlacementStrategy;
import com.fonkwill.fogstorage.client.domain.UploadMode;
import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;
import com.fonkwill.fogstorage.client.encryption.utils.EncryptionUtils;
import com.fonkwill.fogstorage.client.service.exception.ClientServiceException;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.Map;

public class ClientExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(ClientExecutionService.class);

    private Options options = new Options();
    private Option generateKeyOption = new Option("g", false, "Generate private/public Key");
    private Option uploadOption = new Option("u", true, "Upload file");
    private Option downloadOption = new Option("d", true, "Upload file");
    private Option useFogAsStorageOption = new Option("f", false, "Use fog as storage");
    private Option dataChunksCountOption = new Option("c", true, "Number of data chunks (default: 2)");
    private Option parityChunksCountOption = new Option("p", true, "Number of parity chunks (default: 1)");
    private Option hostOption = new Option("h", true, "Host");
    private Option splitOption = new Option("k", true, "Number of kilobytes for splitting up the file");
    private Option encryptionOption = new Option("e", false, "AesEncryptionService enabled");
    private Option scenarioOption = new Option("s", true, "Scenario with given file");
    private Option userOption = new Option("n", true, "Username");
    private Option uiOption = new Option("x", false, "Start UI");

    private CommandLine cmd;

    private FogStorageContext fogStorageContext;

    private FileService fileService;

    public ClientExecutionService(String[] args, FogStorageContext fogStorageContext, FileService fileService) throws ClientServiceException {
        this.fogStorageContext = fogStorageContext;
        options.addOption(uploadOption);
        options.addOption(useFogAsStorageOption);
        options.addOption(dataChunksCountOption);
        options.addOption(parityChunksCountOption);
        options.addOption(hostOption);
        options.addOption(downloadOption);
        options.addOption(splitOption);
        options.addOption(encryptionOption);
        options.addOption(scenarioOption);
        options.addOption(generateKeyOption);
        options.addOption(userOption);
        options.addOption(uiOption);
        CommandLineParser parser = new DefaultParser();

        try {
            this.cmd = parser.parse(options, args);
        } catch (ParseException e) {
            throw new ClientServiceException("Could not parse arguments", e);
        }

        fogStorageContext.clearHosts();
        if (cmd.hasOption(hostOption.getOpt())) {
            String host = cmd.getOptionValue(hostOption.getOpt());
            fogStorageContext.setHosts(host);
        }

        this.fogStorageContext.setSplitMode(isInSplitMode());
        this.fogStorageContext.setCountBytesForSplit(getBytesCountForSplit());
        this.fogStorageContext.setEncryptionMode(isInEcryptionMode());
        this.fogStorageContext.setUsername(getUserName());

        this.fileService = fileService;
    }

    private String getUserName() {
        return cmd.getOptionValue(userOption.getOpt());
    }


    public boolean isUploadMode() {
        return cmd.hasOption(uploadOption.getOpt());
    }

    public String getFile() {
        if (isUploadMode()) {
            return cmd.getOptionValue(uploadOption.getOpt());
        } else {
            return cmd.getOptionValue(downloadOption.getOpt());
        }
    }

    public String getScenarioFile() {
        return cmd.getOptionValue(scenarioOption.getOpt());
    }

    public boolean isInSplitMode() {
        return cmd.hasOption(splitOption.getOpt());
    }

    public boolean isInEcryptionMode() {
        return cmd.hasOption(encryptionOption.getOpt());
    }

    public boolean hasUsername() {return cmd.hasOption(userOption.getOpt());}

    public UploadMode getUploadMode() {

        boolean useFogAsStorage = false;
        if (cmd.hasOption(useFogAsStorageOption.getOpt())) {
            useFogAsStorage = true;
        }
        int dataChunksCount  = extractCount(dataChunksCountOption);
        int parityChunksCount = extractCount(parityChunksCountOption);

        dataChunksCount = (-1 == dataChunksCount) ? 2 : dataChunksCount;
        parityChunksCount = (-1 == parityChunksCount) ? 1 : parityChunksCount;

        UploadMode uploadMode = new UploadMode();
        PlacementStrategy placementStrategy = new PlacementStrategy();
        uploadMode.setPlacementStrategy(placementStrategy);
        placementStrategy.setParityChunksCount(parityChunksCount);
        placementStrategy.setDataChunksCount(dataChunksCount);
        placementStrategy.setUseFogAsStorage(useFogAsStorage);
        return uploadMode;
    }

    private int extractCount(Option chunksCountOption) {
        int result = -1;
        if (cmd.hasOption(chunksCountOption.getOpt())) {
            result = getIntegerFromOptValue(chunksCountOption);
        }
        return result;
    }

    public boolean isDownloadMode() {
        return cmd.hasOption(downloadOption.getOpt());

    }

    public int getBytesCountForSplit() {
        if (!isInSplitMode()) {
            return -1;
        }
        int result = -1;
        result = getIntegerFromOptValue(splitOption);
        result = result * 1024;

        return result;
    }

    private int getIntegerFromOptValue(Option splitOption) {
        int result = -1;
        String value = cmd.getOptionValue(splitOption.getOpt());
        try {
            result = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            System.err.println(value + " is not an integer");
        }
        return result;
    }


    public boolean isScenarioMode() {
        return cmd.hasOption(scenarioOption.getOpt());
    }

    public MeasurementResult execute() {
        if (isKeyGenerationMode()) {
            String privateKey ="";
            String publicKey ="";
            try {
                KeyPair keyPair = EncryptionUtils.getKeyPair(2048);
                privateKey = EncryptionUtils.getPrivateKey(keyPair);
                publicKey = EncryptionUtils.getPublicKey(keyPair);
            } catch (EncryptionException e) {
                System.err.println("Could not generate private/public key pair");
            }

            System.out.println("Public Key: " +  publicKey);
            System.out.println("Private Key: " + privateKey);
            return new MeasurementResult();
        }

        if (isUploadMode()) {

            String file = getFile();

            UploadMode uploadMode = getUploadMode();

            Path path = Paths.get(file);
            MeasurementResult measurement = null;
            try {
                measurement = fileService.upload(path, uploadMode);
            } catch (FileServiceException e) {
                logger.error("Could not upload file", e);
            }
            return measurement;

        } else if(isDownloadMode()) {
            String file = getFile();

            Path path = Paths.get(file);
            MeasurementResult measurementResult = null;
            try {
                measurementResult = fileService.download(path);
            } catch (FileServiceException e) {
                logger.error("Could not download file", e);
            }
            return measurementResult;
        }
        return null;
    }

    private boolean isKeyGenerationMode() {
        return cmd.hasOption(generateKeyOption.getOpt());
    }


    public boolean isUiMode() {
        return cmd.hasOption(uiOption.getOpt());
    }
}
