package com.fonkwill.fogstorage.ui;

import com.fonkwill.fogstorage.client.repository.FogNodeRepository;
import com.fonkwill.fogstorage.client.service.FileService;
import com.fonkwill.fogstorage.client.service.FogStorageContext;
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
