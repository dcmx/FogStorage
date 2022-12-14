package com.fonkwill.fogstorage.client.transferhandler;

import com.fonkwill.fogstorage.client.shared.domain.Placement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class FogStorageServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(FogStorageServiceProvider.class);

    private final FogStorageServiceFactory fogstorageServiceFactory;

    private FogStorageServiceHost onlyService;

    private final List<String> hosts;

    private Set<String> invalid = new HashSet<>();

    private LinkedBlockingQueue<FogStorageServiceHost> availableServices = new LinkedBlockingQueue<>();

    public FogStorageServiceProvider(FogStorageServiceFactory fogStorageServiceFactory, List<String> hosts) {
        this.fogstorageServiceFactory = fogStorageServiceFactory;
        this.hosts = hosts;
        init();
    }

    public Integer getNumberOfavailableServices() {
        if (hosts.size() == 1) {
            return 1;
        }
        return this.availableServices.size();
    }

    private void init() {
        if (hosts.size() == 1) {
            onlyService = fogstorageServiceFactory.getService(hosts.get(0));
        } else {
            for (String host : hosts) {

                FogStorageServiceHost fogStorageServiceHost = fogstorageServiceFactory.getService(host);
                availableServices.add(fogStorageServiceHost);
            }
        }
    }

    public FogStorageServiceHost getService() {
        if (onlyService != null) {
            if (invalid.contains(hosts.get(0))) {
                return null;
            }
            return onlyService;
        }
        FogStorageServiceHost service  = null;
        try {
            service = availableServices.take();
        } catch (InterruptedException e) {
            logger.error("Interrupted Exception");
            return null;
        }
        availableServices.add(service);
        return service;
    }

    public FogStorageServiceHost getServiceForPlacement(Placement placement) {
        if (onlyService != null) {
            if (invalid.contains(hosts.get(0))) {
                return null;
            }
            return onlyService;
        }
        String preferredHost = placement.getStoredAtList().stream()
                .map(a -> a.getNode().getName())
                .filter(hosts::contains)
                .findFirst()
                .orElse(null);

        if (preferredHost != null && !invalid.contains(preferredHost)) {
            return fogstorageServiceFactory.getService(preferredHost);
        } else {
            return getService();
        }

    }

    public void markAsInvalid(FogStorageServiceHost fogStorageService) {
        FogStorageServiceHost fromAvailable;
        if (onlyService != null) {
            fromAvailable = onlyService;
        } else {
            fromAvailable = availableServices.stream()
                    .filter(fh -> fh.getHost().equals(fogStorageService.getHost()))
                    .findFirst()
                    .orElse(null);
        }
        if (fromAvailable == null) {
            //not available
            return;
        }
        logger.info("Marked invalid fogstorageSerivce {}", fromAvailable.getHost());
        invalid.add(fromAvailable.getHost());
        availableServices.remove(fromAvailable);
    }

    public List<String> getHosts() {
        return hosts;
    }

}
