package com.fonkwill.fogstorage.client;

import com.fonkwill.fogstorage.client.config.ApplicationProperties;
import com.fonkwill.fogstorage.client.domain.MeasurementResult;
import com.fonkwill.fogstorage.client.service.ClientExecutionService;
import com.fonkwill.fogstorage.client.service.FileService;
import com.fonkwill.fogstorage.client.service.FogStorageContext;
import com.fonkwill.fogstorage.client.service.exception.ClientServiceException;
import com.fonkwill.fogstorage.scenario.ScenarioRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

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
	    if (args.length == 0) {
	        return;
        }

        ClientExecutionService executionService = null;
	    try {
            executionService = new ClientExecutionService(args, fogStorageContext, fileService);
        } catch (ClientServiceException e) {
	        logger.error(e.getMessage() + e.getCause().getMessage());
	        return;
        }

	    if (executionService.isScenarioMode()) {
	        new ScenarioRunner(fogStorageContext, fileService, executionService.getScenarioFile());
        } else {
            MeasurementResult result  = executionService.execute();
            logMeasurements(result);
        }
    }

    private void logMeasurements(MeasurementResult measurement) {
	    logger.info("Total time {}", measurement.getTotalTime() );
	    for (Map.Entry<String, Long> entry : measurement.getThroughFogNodesTotalTime().entrySet()) {
	        logger.info("Total transfer time through fog node {} : {}", entry.getKey(), entry.getValue());
        }
        logger.info("EnDecryption time: {} ", measurement.getEnDecryptionTime());
        logger.info("Coding time: {}", measurement.getCodingTime());
        logger.info("Total transfer time from fog node: {}", measurement.getFromFogTotalTransferTime());
        logger.info("Placement calculation time : {}", measurement.getPlacementCalculationTime());
        for (Map.Entry<String, Long> entry : measurement.getNodesFromFogTransferTime().entrySet()) {
            logger.info("Transfer time from fog for {} : {}", entry.getKey(), entry.getValue());
        }
    }


}
