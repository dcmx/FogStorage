package com.fonkwill.fogstorage.client.repository.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fonkwill.fogstorage.client.config.ApplicationProperties;
import com.fonkwill.fogstorage.client.domain.NodePublicInfo;
import com.fonkwill.fogstorage.client.domain.NodesPublicConfig;
import com.fonkwill.fogstorage.client.repository.NodePublicInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class NodePublicInfoJsonRepositoryImpl implements NodePublicInfoRepository {

    private static Logger logger = LoggerFactory.getLogger(NodePublicInfoJsonRepositoryImpl.class);

    public NodePublicInfoJsonRepositoryImpl(ApplicationProperties properties, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    private ApplicationProperties properties;

    private ResourceLoader resourceLoader;

    private ObjectMapper objectMapper;


    @Override
    public Set<NodePublicInfo> getAll() {
        NodesPublicConfig nodesPublicConfig = initConfig();
        if (nodesPublicConfig == null) {
            return null;
        }
        return nodesPublicConfig.getNodes();
    }

    private NodesPublicConfig initConfig() {
        Resource configFile = resourceLoader.getResource(properties.getFogNodesConfigFile());
        InputStream configFileIs = null;
        NodesPublicConfig nodesPublicConfig = null;
        try {
            configFileIs = configFile.getInputStream();
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            nodesPublicConfig = objectMapper.readValue(configFileIs, NodesPublicConfig.class);
        } catch (IOException e) {
            logger.error("Could not load fog-node-info file", e);
        } finally {
            if (configFileIs != null) {
                try {
                    configFileIs.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }
        return nodesPublicConfig;
    }
}
