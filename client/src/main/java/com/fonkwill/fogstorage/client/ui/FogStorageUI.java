package com.fonkwill.fogstorage.client.ui;

import com.fonkwill.fogstorage.client.controller.repository.FogNodeRepository;
import com.fonkwill.fogstorage.client.fileservice.service.FileService;
import com.fonkwill.fogstorage.client.controller.FogStorageContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FogStorageUI extends Application {

    private static FogStorageUI instance;

    private static final Logger logger = LoggerFactory.getLogger(FogStorageUI.class);

    private FogStorageContext fogStorageContext;

    private FileService fileService;

    private FogNodeRepository fogNodeRepository;



    public FogStorageUI(){
        instance = this;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();

        LoginController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setFogStorageContext(fogStorageContext);
        controller.setFileService(fileService);
        controller.setFogNodeRepository(fogNodeRepository);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("FogStorage");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static FogStorageUI getInstance() {
        new Thread(() -> startUI(new String[0])).start();
        while (instance == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
               logger.error("Thread interrupted");
            }
        }
        return instance;
    }


    public static void startUI(String[] args){
        launch(args);
    }

    public void setFogStorageContext(FogStorageContext fogStorageContext) {
        this.fogStorageContext = fogStorageContext;
    }
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    public void setFogNodeRepository(FogNodeRepository fogNodeRepository) {
        this.fogNodeRepository = fogNodeRepository;
    }
}
