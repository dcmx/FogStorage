package com.fonkwill.fogstorage.client.client;

import com.fonkwill.fogstorage.client.domain.Node;
import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.service.FogStorageContext;
import com.fonkwill.fogstorage.client.service.exception.FileServiceException;
import com.fonkwill.fogstorage.client.service.impl.FileDownloadService;
import com.fonkwill.fogstorage.client.service.impl.FileUploadService;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class FogStorageServiceProvider {

    Map<String, FogStorageService> serviceMap = new ConcurrentHashMap<>();

    Map<String, Integer> usedMap = new ConcurrentHashMap<>();

    public FogStorageService getService(String host) {
        usedMap.putIfAbsent(host, 0);
        usedMap.computeIfPresent(host, (k, v) -> v = v+1 );

        FogStorageService service = serviceMap.get(host);
        if (service != null) {
            return service;
        }
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                connectTimeout(50, TimeUnit.SECONDS).
                readTimeout(50, TimeUnit.SECONDS).
                writeTimeout(50, TimeUnit.SECONDS).
                build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host).addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(FogStorageService.class);
        serviceMap.put(host, service);
        return service;
    }

    public FogStorageService getServiceForPlacement(Placement placement, List<String> hosts) {
        List<Placement.Assignment> assignmentList = placement.getStoredAtList();
        FogStorageService fogStorageService;
        for (Placement.Assignment assignment : assignmentList) {
            Node node = assignment.getNode();
            for (String host : hosts) {
                if (node.getUrl().startsWith(host)) {
                    return  getService(host);
                }
            }

        }
        String hostToUse = getLeastUsedHost();
        if (hostToUse == null) {
            hostToUse = hosts.get(0);
        }
        return serviceMap.get(hostToUse);

    }

    private String getLeastUsedHost() {
        return usedMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .findFirst()
                .orElse(null)
                .getKey();


    }

    public void clearUsedStatistic() {
        usedMap.clear();
    }
}
