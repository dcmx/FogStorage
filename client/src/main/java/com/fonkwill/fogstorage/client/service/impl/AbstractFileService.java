package com.fonkwill.fogstorage.client.service.impl;

import com.fonkwill.fogstorage.client.client.FogStorageServiceFactory;
import com.fonkwill.fogstorage.client.client.FogStorageServiceProvider;
import com.fonkwill.fogstorage.client.encryption.Decrypter;
import com.fonkwill.fogstorage.client.encryption.Encrypter;
import com.fonkwill.fogstorage.client.encryption.impl.AbstractAesService;
import com.fonkwill.fogstorage.client.encryption.impl.AesEncryptionService;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class AbstractFileService {

    protected boolean encryptionActivated;

    protected Encrypter encrypter;

    protected Decrypter decrypter;

    protected  FogStorageServiceProvider fogStorageServiceProvider;

    protected List<String> hosts;

    protected  TaskExecutor taskExecutor;

    protected  ArrayBlockingQueue<Integer> transferThreads;

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



    public AbstractFileService(FogStorageServiceProvider fogStorageServiceProvider, TaskExecutor taskExecutor) {
        this.fogStorageServiceProvider = fogStorageServiceProvider;
        this.hosts = fogStorageServiceProvider.getHosts();
        this.taskExecutor = taskExecutor;
        int availableServices = fogStorageServiceProvider.getNumberOfavailableServices();
        //one possible runnable for every service
        this.transferThreads = new ArrayBlockingQueue<>( availableServices);
    }
}
