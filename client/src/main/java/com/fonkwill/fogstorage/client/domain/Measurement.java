package com.fonkwill.fogstorage.client.domain;

import okhttp3.Headers;



public class Measurement {

    private static String prefix = "FogStorage-";
    private static String codingTimeKey = "Coding-Time";
    private static String totalTransferTimeKey = "Total-Transfer-Time";
    private static String nodeTransferTimeKey = "Node-Transfer-Time-";
    private static String placementCalculationTimeKey = "Placement-Calculation-Time-";

    private Headers headers;

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
}
