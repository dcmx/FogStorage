package com.fonkwill.fogstorage.client;

import com.fonkwill.fogstorage.client.configuration.ApplicationProperties;
import com.fonkwill.fogstorage.client.domain.Measurement;
import com.fonkwill.fogstorage.client.domain.MeasurementResult;
import com.fonkwill.fogstorage.client.domain.PlacementStrategy;
import com.fonkwill.fogstorage.client.domain.UploadMode;
import com.fonkwill.fogstorage.client.service.ClientExecutionService;
import com.fonkwill.fogstorage.client.service.FileService;
import com.fonkwill.fogstorage.client.service.FogStorageContext;
import com.fonkwill.fogstorage.client.service.exception.ClientServiceException;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Map;


@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class ClientApplication implements CommandLineRunner {

    @Autowired
    private FileService fileService;

    @Autowired
    private FogStorageContext fogStorageContext;

	private static final Logger logger = LoggerFactory.getLogger(ClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

        ClientExecutionService executionService = null;
	    try {
            executionService = new ClientExecutionService(args, fogStorageContext);
        } catch (ClientServiceException e) {
	        System.err.println(e.getMessage() + e.getCause().getMessage());
	        return;
        }

        if (executionService.isUploadMode()) {

            String file = executionService.getFile();

            UploadMode uploadMode = executionService.getUploadMode();

            Path path = Paths.get(file);
            MeasurementResult measurement = null;
            try {
                measurement = fileService.upload(path, uploadMode);
            } catch (FileServiceException e) {
                logger.error("Could not upload file", e);
                return;
            }
            logMeasurements(measurement);

        } else if(executionService.isDownloadMode()) {
            String file = executionService.getFile();

            Path path = Paths.get(file);
            MeasurementResult measurementResult = null;
            try {
                measurementResult = fileService.download(path);
            } catch (FileServiceException e) {
                logger.error("Could not download file", e);
                return;
            }
            logMeasurements(measurementResult);
        } 
	}

    private void logMeasurements(MeasurementResult measurement) {
	    logger.info("EnDecryption time: {} ", measurement.getEnDecryptionTime());
        logger.info("Coding time: {}", measurement.getCodingTime());
        logger.info("Total transfer time: {}", measurement.getTotalTransferTime());
        logger.info("Placement calculation time : {}", measurement.getPlacementCalculationTime());
        for (Map.Entry<String, Long> entry : measurement.getAllNodesTransferTime().entrySet()) {
            logger.info("Transfer time for {} : {}", entry.getKey(), entry.getValue());
        }
    }


}
