package com.fonkwill.fogstorage.client.transferhandler;

import com.fonkwill.fogstorage.client.transferhandler.authenticaton.FogStorageAuthenticator;
import com.fonkwill.fogstorage.client.shared.domain.FogNode;
import com.fonkwill.fogstorage.client.shared.domain.SharedSecret;
import com.fonkwill.fogstorage.client.cryptoservice.exception.EncryptionException;
import com.fonkwill.fogstorage.client.cryptoservice.impl.AbstractAesService;
import com.fonkwill.fogstorage.client.cryptoservice.utils.EncryptionUtils;
import com.fonkwill.fogstorage.client.controller.repository.FogNodeRepository;
import com.fonkwill.fogstorage.client.cryptoservice.CryptoService;
import com.fonkwill.fogstorage.client.controller.FogStorageContext;
import com.fonkwill.fogstorage.client.fileservice.exception.FileServiceException;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class FogStorageServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(FogStorageServiceFactory.class);

    private FogNodeRepository fogNodeRepository;

    private Map<String, FogNode> fogNodeMap;

    private FogStorageContext fogStorageContext;

    private CryptoService enDeCryptionService;

    public FogStorageServiceFactory(FogNodeRepository fogNodeRepository, FogStorageContext fogStorageContext, CryptoService enDeCryptionService) {
        this.fogStorageContext = fogStorageContext;
        this.fogNodeRepository = fogNodeRepository;
        this.enDeCryptionService = enDeCryptionService;
        try {
            init();
        } catch (FileServiceException e) {
            logger.error("Could not initiate FogStorageFactory");
        }
    }

    private void init() throws FileServiceException {
        fogNodeMap = new ConcurrentHashMap<>();
        Set<FogNode> fogNodes = fogNodeRepository.getAll();
        for (FogNode fogNode : fogNodes) {
            fogNodeMap.put(fogNode.getName(), fogNode);
        }
    }

    private Map<String, FogStorageServiceHost> serviceMap = new ConcurrentHashMap<>();

    public FogStorageServiceHost getService(String host) {

        FogStorageServiceHost service = serviceMap.get(host);
        if (service != null) {
            return service;
        }
        FogNode fogNode = fogNodeMap.get(host);
        if (fogNode == null) {
            return null;
        }
        String url = fogNode.getUrl();

        SharedSecret sharedSecret = new SharedSecret();
        try {
            String sharedSecretString = EncryptionUtils.getKey(AbstractAesService.keyLengthBit, AbstractAesService.algorithm);
            sharedSecret.setValue(sharedSecretString);
        } catch (EncryptionException e) {
            logger.error("Could not generate shhared secret");
            return null;
        }
        FogStorageAuthenticator fogStorageAuthenticator = new FogStorageAuthenticator(url, sharedSecret, fogNode.getPublicKey(), fogStorageContext, enDeCryptionService);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                authenticator(fogStorageAuthenticator).
                addInterceptor(fogStorageAuthenticator).
                connectTimeout(50, TimeUnit.SECONDS).
                readTimeout(50, TimeUnit.SECONDS).
                writeTimeout(50, TimeUnit.SECONDS).
                build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url).addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        FogStorageFileService fileService = retrofit.create(FogStorageFileService.class);
        FogStorageServiceHost fogStorageServiceHost = new FogStorageServiceHost(fileService, host, sharedSecret);

        serviceMap.put(host, fogStorageServiceHost);
        return fogStorageServiceHost;
    }

}
