package com.fonkwill.fogstorage.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String clientName;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getFogNodesConfigFile() {
        return fogNodesConfigFile;
    }

    public void setFogNodesConfigFile(String fogNodesConfigFile) {
        this.fogNodesConfigFile = fogNodesConfigFile;
    }

    private String fogNodesConfigFile;

}
