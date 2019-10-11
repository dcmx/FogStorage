package com.fonkwill.fogstorage.client.shared.domain;

public class PlacementStrategy {

    private int dataChunksCount;

    private int parityChunksCount;

    private boolean useFogAsStorage;

    public boolean isUseFogAsStorage() {
        return useFogAsStorage;
    }

    public void setUseFogAsStorage(boolean useFogAsStorage) {
        this.useFogAsStorage = useFogAsStorage;
    }

    public int getDataChunksCount() {
        return dataChunksCount;
    }

    public void setDataChunksCount(int dataChunksCount) {
        this.dataChunksCount = dataChunksCount;
    }

    public int getParityChunksCount() {
        return parityChunksCount;
    }

    public void setParityChunksCount(int parityChunksCount) {
        this.parityChunksCount = parityChunksCount;
    }
}
