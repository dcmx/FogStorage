package com.fonkwill.fogstorage.client.domain;


import java.util.Objects;

public class NodePublicInfo {

    private String name;

    private String url;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NodePublicInfo name(String name) {
        this.name = name;
        return this;
    }

    public NodePublicInfo url(String url) {
        this.url = url;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodePublicInfo that = (NodePublicInfo) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url);
    }
}
