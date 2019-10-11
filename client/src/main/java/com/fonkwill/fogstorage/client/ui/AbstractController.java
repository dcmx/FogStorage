package com.fonkwill.fogstorage.client.ui;

import com.fonkwill.fogstorage.client.controller.repository.FogNodeRepository;
import com.fonkwill.fogstorage.client.fileservice.service.FileService;
import com.fonkwill.fogstorage.client.controller.FogStorageContext;
import javafx.stage.Stage;

public class AbstractController {

    protected FileService fileService;

    protected FogStorageContext fogStorageContext;

    protected Stage primaryStage;

    protected FogNodeRepository fogNodeRepository;


    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
