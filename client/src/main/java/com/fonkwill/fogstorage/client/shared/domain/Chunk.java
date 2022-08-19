package com.fonkwill.fogstorage.client.shared.domain;

import java.io.Serializable;

/**
 * A Chunk.
 */

public class Chunk implements Serializable {

    private static final long serialVersionUID = 1L;


    private String uuid;


    private Long size;


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int position;

    public String getUuid() {
        return uuid;
    }

    public Chunk uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getSize() {
        return size;
    }

    public Chunk size(Long size) {
        this.size = size;
        return this;
    }

    public void setSize(Long size) {
        this.size = size;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chunk)) {
            return false;
        }
        return uuid != null && uuid.equals(((Chunk) o).uuid);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Chunk{" +
            ", uuid='" + getUuid() + "'" +
            ", size=" + getSize() +
            "}";
    }
}
