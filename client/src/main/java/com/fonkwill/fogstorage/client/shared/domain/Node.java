package com.fonkwill.fogstorage.client.shared.domain;

import com.fonkwill.fogstorage.client.shared.domain.enumeration.FogStorageNodeType;

import java.io.Serializable;


/**
 * A Node.
 */

public class Node implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String url;

    private FogStorageNodeType type;

    public String getName() {
        return name;
    }

    public Node name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public Node url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FogStorageNodeType getType() {
        return type;
    }

    public Node type(FogStorageNodeType type) {
        this.type = type;
        return this;
    }

    public void setType(FogStorageNodeType type) {
        this.type = type;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        return name != null && name.equals(((Node) o).name);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Node{" +
                " name='" + getName() + "'" +
                ", url='" + getUrl() + "'" +
                ", type='" + getType() + "'" +
                "}";
    }

}
