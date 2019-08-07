package com.fonkwill.fogstorage.scenario;

import com.fonkwill.fogstorage.client.ClientApplication;
import com.fonkwill.fogstorage.client.service.ClientExecutionService;
import com.fonkwill.fogstorage.client.service.FileService;
import com.fonkwill.fogstorage.client.service.FogStorageContext;
import com.fonkwill.fogstorage.client.service.exception.ClientServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScenarioRunner {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioRunner.class);

    private FogStorageContext fogStorageContext;

    private FileService fileService;

    private String scenarioFile;


    public ScenarioRunner(FogStorageContext fogStorageContext, FileService fileService, String scenarioFile) {
        this.fogStorageContext = fogStorageContext;
        this.fileService = fileService;
        this.scenarioFile = scenarioFile;
        init();
    }


    private void init() {
        Path scenarioPath = Paths.get(scenarioFile);


        List<String[]> commands = new ArrayList<>();
        try(Stream<String> content = Files.lines(scenarioPath)){

            commands = content.filter(cmd -> !cmd.startsWith("//")).map(cmd -> cmd.split(" ")).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Could not read in scenario file", e);
            return;
        }

        ClientExecutionService executionService;
        for (String [] args : commands) {
            logger.info("Running with args", args);
            try {
                executionService = new ClientExecutionService(args, fogStorageContext, fileService);
                executionService.execute();
                Thread.sleep(5000);
            } catch (ClientServiceException e) {
                logger.error("Could not run with args: ", args);
                break;
            } catch (InterruptedException e) {
                logger.error("Thread was interrupted");
                continue;
            }
        }


    }


}
