package com.fonkwill.fogstorage.client.domain;

import okhttp3.Headers;

import java.util.HashMap;
import java.util.Map;


public class Measurement {

    private static String prefix = "FogStorage-";
    private static String codingTimeKey = "Coding-Time";
    private static String totalTransferTimeKey = "Total-Transfer-Time";
    private static String nodeTransferTimeKey = "Node-Transfer-Time-";
    private static String placementCalculationTimeKey = "Placement-Calculation-Time-";

    private Headers headers;

    private Long enDecryptionTime = 0L;

    public Measurement(Headers headers) {
        this.headers = headers;
    }

    public String getCodingTime(){
        return headers.get(prefix + codingTimeKey);
    }

    public String getTotalTransferTime() {
        return headers.get(prefix + totalTransferTimeKey );
    }

    public String getNodeTransferTime(String nodeKey) {
        return headers.get(prefix + nodeTransferTimeKey + nodeKey);
    }

    public String getPlacementCalculationTime() {
        return headers.get(prefix + placementCalculationTimeKey);
    }

    public Map<String, String> getAllNodesTransferTime() {
        if (headers == null) {
            return null;
        }
        String nodeTransferTime = prefix + nodeTransferTimeKey;
        int length = nodeTransferTime.length();

        Map<String, String> result = new HashMap<>();
        for (String name : headers.names()){
            if (name.startsWith(nodeTransferTime)) {
                String node = name.substring(length);
                result.put(node, getNodeTransferTime(node));
            }
        }
        return result;

    }


    public void setEnDecryptionTime(long encryptionTime) {
        this.enDecryptionTime = encryptionTime;
    }

    public Long getEnDecryptionTime() {
        return enDecryptionTime;
    }
}
