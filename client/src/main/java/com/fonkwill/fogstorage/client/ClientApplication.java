package com.fonkwill.fogstorage.client;

import com.fonkwill.fogstorage.client.shared.config.ApplicationProperties;
import com.fonkwill.fogstorage.client.shared.domain.MeasurementResult;
import com.fonkwill.fogstorage.client.controller.repository.FogNodeRepository;
import com.fonkwill.fogstorage.client.controller.ClientExecutionService;
import com.fonkwill.fogstorage.client.fileservice.service.FileService;
import com.fonkwill.fogstorage.client.controller.FogStorageContext;
import com.fonkwill.fogstorage.client.fileservice.exception.ClientServiceException;
import com.fonkwill.fogstorage.client.scenario.ScenarioRunner;
import com.fonkwill.fogstorage.client.ui.FogStorageUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.Console;
import java.util.Map;
import java.util.Scanner;


@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class ClientApplication implements CommandLineRunner {

    @Autowired
    private FileService fileService;

    @Autowired
    private FogStorageContext fogStorageContext;


    @Autowired
    private FogNodeRepository fogNodeRepository;

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
	    if (executionService.isUiMode()){
	        FogStorageUI fogStorageUI = FogStorageUI.getInstance();

	        fogStorageUI.setFogStorageContext(fogStorageContext);
	        fogStorageUI.setFileService(fileService);
	        fogStorageUI.setFogNodeRepository(fogNodeRepository);
	        return;
        }


	    if (!executionService.hasUsername()) {
	        logger.error("No username give");
	        return;
        }

        Console console = System.console();
        String enteredPassword;
        if (console != null) {
            enteredPassword = new String(console.readPassword("Password:"));
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Password:");
            enteredPassword = scanner.next();
        }
	    fogStorageContext.setPassword(enteredPassword);

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
