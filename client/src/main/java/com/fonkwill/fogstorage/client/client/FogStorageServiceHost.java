package com.fonkwill.fogstorage.client.client;

public class FogStorageServiceHost {

    public FogStorageServiceHost(FogStorageService fogStorageService, String host) {
        this.fogStorageService = fogStorageService;
        this.host = host;
    }

    public FogStorageService getFogStorageService() {
        return fogStorageService;
    }

    public String getHost() {
        return host;
    }

    private FogStorageService fogStorageService;

    private String host;

}
