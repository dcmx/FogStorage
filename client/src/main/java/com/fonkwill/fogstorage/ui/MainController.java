package com.fonkwill.fogstorage.ui;

import com.fonkwill.fogstorage.client.domain.FogNode;
import com.fonkwill.fogstorage.client.domain.MeasurementResult;
import com.fonkwill.fogstorage.client.encryption.impl.AbstractAesService;
import com.fonkwill.fogstorage.client.service.ClientExecutionService;
import com.fonkwill.fogstorage.client.service.exception.ClientServiceException;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.applet.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private Stage primaryStage;

    @FXML
    private HBox p_checkboxes;

    @FXML
    private Label l_filename;

    @FXML
    private ToggleButton tb_encryption;

    @FXML
    private ToggleButton tb_fogstorage;

    @FXML
    private Slider i_dataChunks;

    @FXML
    private Slider i_parityChunks;

    @FXML
    private TextArea tf_result;

    private File file;

    @FXML
    private Slider i_blockSize;

    @FXML
    private Label l_blockSize;

    @FXML
    private Tab tab_download;

    @FXML
    private Tab tab_upload;

    private FileChooser fileChooser = new FileChooser();

    private List<CheckBox> checkboxes;

    public void init() {

        this.checkboxes = new ArrayList<>();
        try {
            for (FogNode node : fogNodeRepository.getAll()) {
                CheckBox c = new CheckBox();
                c.textProperty().setValue(node.getName());
                c.setAllowIndeterminate(false);
                checkboxes.add(c);
            }
        } catch (FileServiceException e) {
            logger.error("Could not load fog nodes");
        }
        p_checkboxes.getChildren().addAll(checkboxes);

        i_blockSize.valueProperty().addListener((observable, ov, nv) -> {
            l_blockSize.textProperty().setValue(nv.toString());
        });

    }

    public void onButtonChooseFile(ActionEvent event) {
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            l_filename.textProperty().setValue(file.getAbsolutePath());
            this.file = file;
        }

    }

    public void onButtonExecute(ActionEvent event){
        String argsCommand = "";
        if (tab_upload.isSelected()) {
            argsCommand += "-u "+ file.getAbsolutePath() + " ";
            if (tb_fogstorage.isSelected()) {
                argsCommand +="-f ";
            }
            if (tb_encryption.isSelected()) {
                argsCommand +="-e ";
            }

            argsCommand +="-c "+ (int) i_dataChunks.getValue() + " ";
            argsCommand +="-p "+ (int) i_parityChunks.getValue() + " ";
            argsCommand +="-k "+ (int) i_blockSize.getValue() + " ";

        } else if (tab_download.isSelected()) {
            argsCommand += "-d "+ file.getAbsolutePath() + " ";
        }
        List<String> chosenHosts = p_checkboxes.getChildren().stream().map(cb -> (CheckBox) cb).filter(CheckBox::isSelected).map(cb -> cb.textProperty().get()).collect(Collectors.toList());
        String chosenHostsString = String.join(",",chosenHosts);
        argsCommand +="-h "+chosenHostsString + " ";
        argsCommand +="-n "+fogStorageContext.getUsername();

        String[] args = argsCommand.split(" ");




        try {
            ClientExecutionService clientExecutionService = new ClientExecutionService(args, fogStorageContext, fileService);
            MeasurementResult measurementResult = clientExecutionService.execute();

            String result = print(measurementResult);
            tf_result.textProperty().setValue(result);
        } catch (ClientServiceException e) {
            logger.error("Could not instantiate clientExecutionService");
        }


    }

    private String print(MeasurementResult measurement) {
        StringBuilder builder = new StringBuilder();
        String line;
        line = "Total time" + measurement.getTotalTime() + "\n";
        builder.append(line);
        for (Map.Entry<String, Long> entry : measurement.getThroughFogNodesTotalTime().entrySet()) {
            line = "Total transfer time through fog node " + entry.getKey() + " :  " + entry.getValue() +" \n";
            builder.append(line);
        }
        line = "EnDecryption time: " + measurement.getEnDecryptionTime()  + "\n";
        builder.append(line);
        line = "Coding time: " + measurement.getCodingTime()  + "\n";;
        builder.append(line);
        line = "Total transfer time from fog node: " + measurement.getFromFogTotalTransferTime()  + "\n";;
        builder.append(line);
        line = "Placement calculation time : " + measurement.getPlacementCalculationTime()  + "\n";;
        builder.append(line);
        for (Map.Entry<String, Long> entry : measurement.getNodesFromFogTransferTime().entrySet()) {
            line = "Transfer time from fog for " + entry.getKey() + ":" + entry.getValue() + "\n";
            builder.append(line);
        }
        return builder.toString();
    }


    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
