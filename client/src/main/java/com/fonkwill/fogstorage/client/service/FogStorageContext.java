package com.fonkwill.fogstorage.client.service;

import org.springframework.stereotype.Component;

@Component
public class FogStorageContext {
    private String host;

    private boolean splitMode;



    private int countBytesForSplit;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getCountBytesForSplit() {
        return countBytesForSplit;
    }

    public void setCountBytesForSplit(int countBytesForSplit) {
        this.countBytesForSplit = countBytesForSplit;
    }


    public void setSplitMode(boolean inSplitMode) {
        this.splitMode = inSplitMode;
    }
    public boolean isSplitMode() {
        return splitMode;
    }

}
