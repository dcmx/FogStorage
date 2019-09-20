package com.fonkwill.fogstorage.scenario;

import com.fonkwill.fogstorage.client.ClientApplication;
import com.fonkwill.fogstorage.client.domain.Measurement;
import com.fonkwill.fogstorage.client.domain.MeasurementResult;
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
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScenarioRunner {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioRunner.class);

    private FogStorageContext fogStorageContext;

    private FileService fileService;

    private String scenarioFile;

    private ResultContainer resultContainer;

    private static Random random = new Random();

    public ScenarioRunner(FogStorageContext fogStorageContext, FileService fileService, String scenarioFile) {
        this.fogStorageContext = fogStorageContext;
        this.fileService = fileService;
        this.scenarioFile = scenarioFile;
        this.resultContainer = new ResultContainer(scenarioFile);
        init();
    }


    private void init() {
        Path scenarioPath = Paths.get(scenarioFile);

        List<String> variables = new ArrayList<>();
        List<String> plainCommands = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        try(Stream<String> content = Files.lines(scenarioPath)){
            content.forEach(
                    (line) -> {
                        if (line.startsWith("{")){
                            variables.add(line);
                        } else if (!line.startsWith("//") && !line.isEmpty()){
                            plainCommands.add(line);
                        }
                    }
             );

            for (String plainCommand : plainCommands) {
                String command = plainCommand;
                for (String variable : variables) {
                    int colon = variable.indexOf(":");
                    String key = variable.substring(0, colon);
                    String value  = variable.substring(colon+1);
                    command = command.replace(key, value);
                }
                commands.add(command);
            }



        } catch (IOException e) {
            logger.error("Could not read in scenario file", e);
            return;
        }


        ClientExecutionService executionService;
        resultContainer.setVariables(variables);
        for (String command : commands) {
            logger.info("Running with args {}", command);

            int colon = command.indexOf(":");
            String title = command.substring(0, colon);
            String cmd = command.substring(colon+1);

            int multiplier = 1;
            int multiplySign = command.indexOf("x");
            if (multiplySign > -1 && multiplySign < colon ) {
                String multiplyString = command.substring(0, multiplySign);
                multiplier = Integer.valueOf(multiplyString);
                title = title.substring(multiplySign+1);
            }
        try {

            for (int i  = 0; i <= multiplier; i ++) {
                String args[] = cmd.split(" ");
                String file = null;
                boolean isFileName = false;
                int pos = 0;
                int x = 0;
                for (String arg : args) {
                    if (isFileName) {
                        file = arg;
                        pos = x;
                    }
                    if (arg.contains("-u") || arg.contains("-d")){
                        isFileName = true;
                    } else{
                        isFileName = false;
                    }
                    x++;
                }
                String newFileName = file;
                if (file.endsWith(".fog")){
                    newFileName = file.substring(0, file.length() - 4);
                    newFileName += "_"+ i + ".fog";
                } else {
                    newFileName += "_" + i;
                }
                args[pos] = newFileName;

                executionService = new ClientExecutionService(args, fogStorageContext, fileService);

                if (cmd.contains("-u")){
                    scrambleFile(file, i);
                }
                MeasurementResult measurementResult = executionService.execute();
                if (i > 0 ) {

                    if (cmd.contains("-u")) {
                        resultContainer.addUploadMeasurmentResult(title, measurementResult);
                    } else if (cmd.contains("-d")) {
                        resultContainer.addDownloadMeasurmentResult(title, measurementResult);
                    }
                    Thread.sleep(2000);
                } else {
                    logger.info("Warmed Up");
                }
            }

            } catch (ClientServiceException e) {
                logger.error("Could not run with args: {}", command);
                break;
            } catch (InterruptedException e) {
                logger.error("Thread was interrupted");
                continue;
            }
        }
        resultContainer.createResultFile();


    }

    private void scrambleFile(String file, int i) {
        Path filePath = Paths.get(file);
        Long fileSize = filePath.toFile().length();
        if (fileSize > Integer.MAX_VALUE){
            logger.error("File too big to scramble");
            return;
        }
        Path newFilePath = Paths.get(file+"_"+i);
        byte[] newFile = new byte[fileSize.intValue()];
        random.nextBytes(newFile);
        try {
            Files.write(newFilePath, newFile);
        } catch (IOException e) {
            logger.error("Could not scramble file");
        }


    }


}
