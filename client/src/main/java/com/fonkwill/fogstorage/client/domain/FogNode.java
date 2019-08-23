package com.fonkwill.fogstorage.client.domain;


import java.util.Objects;

public class FogNode {

    private String name;

    private String url;

    private String publicKey;

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

    public FogNode name(String name) {
        this.name = name;
        return this;
    }

    public FogNode url(String url) {
        this.url = url;
        return this;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FogNode that = (FogNode) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url);
    }

}
