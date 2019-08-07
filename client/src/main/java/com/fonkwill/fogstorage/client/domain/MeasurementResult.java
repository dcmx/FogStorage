package com.fonkwill.fogstorage.client.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MeasurementResult {

    private Long codingTime = 0L;

    private Long totalTransferTime = 0L;

    private Long placementCalculationTime = 0L;

    private Long enDecryptionTime = 0L;

    private Map<String, Long> allNodesTransferTime = new HashMap<>();

    public void addMeasurement(Measurement measurement) {
        codingTime += Long.valueOf(measurement.getCodingTime());
        totalTransferTime += Long.valueOf(measurement.getTotalTransferTime());

        String currentCalculationTime = measurement.getPlacementCalculationTime();
        if (currentCalculationTime == null) {
            currentCalculationTime = "0";
        }
        placementCalculationTime += Long.valueOf(currentCalculationTime);

        for (Map.Entry<String, String> nodesTime: measurement.getAllNodesTransferTime().entrySet()) {
            Long alreadyStoredForNode = allNodesTransferTime.get(nodesTime.getKey());

            if (alreadyStoredForNode != null) {
                alreadyStoredForNode += Long.valueOf(nodesTime.getValue());
                allNodesTransferTime.put(nodesTime.getKey(), alreadyStoredForNode);
            } else {
                allNodesTransferTime.put(nodesTime.getKey(), Long.valueOf(nodesTime.getValue()));
            }
        }

        enDecryptionTime += measurement.getEnDecryptionTime();
    }


    public String getCodingTime() {
        return codingTime.toString();
    }

    public String getTotalTransferTime() {
        return totalTransferTime.toString();
    }

    public String getPlacementCalculationTime() {
        return placementCalculationTime.toString();
    }

    public Map<String, Long> getAllNodesTransferTime() {
        return  allNodesTransferTime;
    }

    public String getEnDecryptionTime() {
        return enDecryptionTime.toString();
    }
}
