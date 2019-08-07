package com.fonkwill.fogstorage.client.domain;

import java.util.ArrayList;
import java.util.List;

public class RegenerationInfo {

    List<Placement> placementList = new ArrayList<>();



    private String fileName;

    private long fileSize;

    private String key;

    public int getBytesOfPart() {
        return bytesOfPart;
    }

    public void setBytesOfPart(int bytesOfPart) {
        this.bytesOfPart = bytesOfPart;
    }

    private int bytesOfPart = -1;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Placement> getPlacementList() {
        return placementList;
    }

    public void setPlacementList(List<Placement> placementList) {
        this.placementList = placementList;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
