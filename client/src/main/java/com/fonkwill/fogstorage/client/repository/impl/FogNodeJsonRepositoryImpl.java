package com.fonkwill.fogstorage.client.repository.impl;

;
import com.fonkwill.fogstorage.client.config.ApplicationProperties;
import com.fonkwill.fogstorage.client.domain.FogNode;
import com.fonkwill.fogstorage.client.domain.FogNodeConfig;
import com.fonkwill.fogstorage.client.repository.FogNodeRepository;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

@Component
public class FogNodeJsonRepositoryImpl implements FogNodeRepository {

    private static Logger logger = LoggerFactory.getLogger(FogNodeJsonRepositoryImpl.class);

    public FogNodeJsonRepositoryImpl(ApplicationProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    private ApplicationProperties properties;

    private ResourceLoader resourceLoader;



    @Override
    public Set<FogNode> getAll() throws FileServiceException {
        FogNodeConfig fogNodeConfig = initConfig();
        if (fogNodeConfig == null) {
            return null;
        }
        return fogNodeConfig.getNodes();
    }

    private FogNodeConfig initConfig() throws FileServiceException {
        Resource configFile = resourceLoader.getResource(properties.getFogNodesConfigFile());

        FogNodeConfig fogNodeConfig;
        Gson gson = new Gson();
        try (FileReader fileReader = new FileReader(configFile.getFile())){
            fogNodeConfig = gson.fromJson(fileReader, FogNodeConfig.class);
        } catch (IOException e) {
            throw new FileServiceException("Could not read in nodes config file!");
        }
        return fogNodeConfig;
    }
}
