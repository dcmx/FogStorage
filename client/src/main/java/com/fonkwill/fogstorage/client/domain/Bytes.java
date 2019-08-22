package com.fonkwill.fogstorage.client.domain;

import java.util.Arrays;

public class Bytes {


    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Bytes copy() {
        byte[] newContent =  Arrays.copyOf(this.content, content.length);
        Bytes newBytes = new Bytes();
        newBytes.setContent(newContent);
        return newBytes;
    }
}
