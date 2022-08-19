package com.fonkwill.fogstorage.client.shared.domain;

public class SecretUserAttributes {

    private String password;

    private String sharedSecret;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }
}
