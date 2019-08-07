package com.fonkwill.fogstorage.client.service.impl;

import com.fonkwill.fogstorage.client.encryption.Decrypter;
import com.fonkwill.fogstorage.client.encryption.Encrypter;
import com.fonkwill.fogstorage.client.encryption.impl.AbstractAesService;
import com.fonkwill.fogstorage.client.encryption.impl.AesEncryptionService;

public abstract class AbstractFileService {

    protected boolean encryptionActivated;

    protected Encrypter encrypter;

    protected Decrypter decrypter;

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
}
