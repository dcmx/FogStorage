package com.fonkwill.fogstorage.client.client;

import com.fonkwill.fogstorage.client.domain.FogNode;
import com.fonkwill.fogstorage.client.repository.FogNodeRepository;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class FogStorageServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(FogStorageServiceFactory.class);

    private FogNodeRepository fogNodeRepository;

    private Map<String, FogNode> fogNodeMap;

    public FogStorageServiceFactory(FogNodeRepository fogNodeRepository) {
        this.fogNodeRepository = fogNodeRepository;
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

    private Map<String, FogStorageService> serviceMap = new ConcurrentHashMap<>();

    public FogStorageService getService(String host) {

        FogStorageService service = serviceMap.get(host);
        if (service != null) {
            return service;
        }
        FogNode fogNode = fogNodeMap.get(host);
        if (fogNode == null) {
            return null;
        }
        String url = fogNode.getUrl();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                connectTimeout(50, TimeUnit.SECONDS).
                readTimeout(50, TimeUnit.SECONDS).
                writeTimeout(50, TimeUnit.SECONDS).
                build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url).addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(FogStorageService.class);
        serviceMap.put(host, service);
        return service;
    }

}
