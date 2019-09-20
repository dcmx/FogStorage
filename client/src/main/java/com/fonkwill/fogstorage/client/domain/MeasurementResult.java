package com.fonkwill.fogstorage.client.domain;

import java.util.HashMap;
import java.util.Map;

public class MeasurementResult {

    private Long codingTime = 0L;

    private Long fileSize = 0L;

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    private Long totalTime = 0L;

    private Long fromFogTotalTransferTime = 0L;

    private Long placementCalculationTime = 0L;

    private Long enDecryptionTime = 0L;

    private Map<String, Long> nodesFromFogTransferTime = new HashMap<>();

    private Map<String, Long> throughFogNodesTotalTime = new HashMap<>();

    public void addMeasurement(Measurement measurement) {
        codingTime += Long.valueOf(measurement.getCodingTime());
        fromFogTotalTransferTime += Long.valueOf(measurement.getTotalTransferTimeFromFogNode());

        String currentCalculationTime = measurement.getPlacementCalculationTime();
        if (currentCalculationTime == null) {
            currentCalculationTime = "0";
        }
        placementCalculationTime += Long.valueOf(currentCalculationTime);

        for (Map.Entry<String, String> nodesTime: measurement.getAllNodesTransferTimeFromFogNode().entrySet()) {
            Long alreadyStoredForNode = nodesFromFogTransferTime.get(nodesTime.getKey());

            if (alreadyStoredForNode != null) {
                alreadyStoredForNode += Long.valueOf(nodesTime.getValue());
                nodesFromFogTransferTime.put(nodesTime.getKey(), alreadyStoredForNode);
            } else {
                nodesFromFogTransferTime.put(nodesTime.getKey(), Long.valueOf(nodesTime.getValue()));
            }
        }

        enDecryptionTime += measurement.getEnDecryptionTime();

        for (Map.Entry<String, Long> fogNodeTimeEntry : measurement.getThroughFogNodeTotalTime().entrySet()) {
            String fogNode = fogNodeTimeEntry.getKey();
            Long throughFogNodeTime = fogNodeTimeEntry.getValue();
            Long throughFogNodeTimeStored = throughFogNodesTotalTime.get(fogNode);
            if (throughFogNodeTimeStored != null) {
                throughFogNodeTime = throughFogNodeTimeStored + throughFogNodeTime;
            }
            throughFogNodesTotalTime.put(fogNode, throughFogNodeTime);
        }
    }


    public String getCodingTime() {
        return codingTime.toString();
    }

    public String getFromFogTotalTransferTime() {
        return fromFogTotalTransferTime.toString();
    }

    public String getPlacementCalculationTime() {
        return placementCalculationTime.toString();
    }

    public Map<String, Long> getNodesFromFogTransferTime() {
        return nodesFromFogTransferTime;
    }

    public String getEnDecryptionTime() {
        return enDecryptionTime.toString();
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public Map<String, Long> getThroughFogNodesTotalTime() {
        return throughFogNodesTotalTime;
    }

}
