package com.fonkwill.fogstorage.client.transferhandler;

import com.fonkwill.fogstorage.client.shared.domain.SharedSecret;

public class FogStorageServiceHost {

    private FogStorageFileService fogStorageFileService;

    private String host;

    private SharedSecret sharedSecret;


    public FogStorageServiceHost(FogStorageFileService fogStorageFileService, String host, SharedSecret sharedSecret) {
        this.fogStorageFileService = fogStorageFileService;
        this.host = host;
        this.sharedSecret = sharedSecret;

    }

    public FogStorageFileService getFogStorageFileService() {
        return fogStorageFileService;
    }

    public String getHost() {
        return host;
    }


    public SharedSecret getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(SharedSecret sharedSecret) {
        this.sharedSecret = sharedSecret;
    }
}
