package com.fonkwill.fogstorage.client.service.impl;

import com.fonkwill.fogstorage.client.client.FogStorageServiceProvider;
import com.fonkwill.fogstorage.client.encryption.Decrypter;
import com.fonkwill.fogstorage.client.encryption.Encrypter;
import com.fonkwill.fogstorage.client.encryption.impl.AbstractAesService;
import com.fonkwill.fogstorage.client.encryption.impl.AesEncryptionService;

import java.util.List;

public abstract class AbstractFileService {

    protected boolean encryptionActivated;

    protected Encrypter encrypter;

    protected Decrypter decrypter;

    protected  FogStorageServiceProvider fogStorageServiceProvider;

    protected  List<String> hosts;

    public void enableEncryption() {
        this.encryptionActivated = true;
    }

    public void disableEncryption() {
        this.encryptionActivated = false;
    }

    public void setEncrypter(Encrypter encryptionService) {
        this.encrypter = encryptionService;
    }
    
    public void setDecrypter(Decrypter decrypter) {
        this.decrypter = decrypter;
    }

    public AbstractFileService(FogStorageServiceProvider fogStorageServiceProvider, List<String> hosts) {
        this.fogStorageServiceProvider = fogStorageServiceProvider;
        this.hosts =  hosts;
    }
}
