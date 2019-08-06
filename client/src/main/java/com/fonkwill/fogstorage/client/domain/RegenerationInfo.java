package com.fonkwill.fogstorage.client.domain;

import java.util.ArrayList;
import java.util.List;

public class RegenerationInfo {

    List<Placement> placementList = new ArrayList<>();

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    private long fileSize;

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

    private String fileName;

    public List<Placement> getPlacementList() {
        return placementList;
    }

    public void setPlacementList(List<Placement> placementList) {
        this.placementList = placementList;
    }




}
