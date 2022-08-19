package com.fonkwill.fogstorage.client.shared.domain;

public class FileInfo {

    private String checksum;

    private Long size;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }


}
