package com.fonkwill.fogstorage.client.fileservice.service.impl;

import com.fonkwill.fogstorage.client.transferhandler.FogStorageServiceProvider;
import com.fonkwill.fogstorage.client.cryptoservice.Decrypter;
import com.fonkwill.fogstorage.client.cryptoservice.Encrypter;
import com.fonkwill.fogstorage.client.cryptoservice.CryptoService;
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

    protected CryptoService enDeCryptionService;

    protected int threadsPerService;

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



    public AbstractFileService(FogStorageServiceProvider fogStorageServiceProvider, TaskExecutor taskExecutor, CryptoService enDeCryptionService, int threadsPerService) {
        this.threadsPerService = threadsPerService;
        this.fogStorageServiceProvider = fogStorageServiceProvider;
        this.hosts = fogStorageServiceProvider.getHosts();
        this.taskExecutor = taskExecutor;
        int availableServices = fogStorageServiceProvider.getNumberOfavailableServices();
        //one possible runnable for every service
        this.transferThreads = new ArrayBlockingQueue<>( availableServices * threadsPerService );
        this.enDeCryptionService = enDeCryptionService;
    }
}
