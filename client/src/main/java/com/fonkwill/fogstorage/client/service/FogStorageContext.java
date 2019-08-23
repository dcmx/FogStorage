package com.fonkwill.fogstorage.client.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FogStorageContext {
    private String host;

    private boolean splitMode;

    private String username;

    private String password;

    private int countBytesForSplit;
    private boolean encryptionMode;

    public List<String> getHosts() {
        return hosts;
    }

    private List<String> hosts = new ArrayList<>();

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

    public void setEncryptionMode(boolean inEcryptionMode) {
        this.encryptionMode = inEcryptionMode;
    }

    public boolean isEncryptionMode() {
        return encryptionMode;
    }

    public void clearHosts() {
        hosts.clear();
    }

    public void setHosts(String hosts) {
        if (hosts.contains(",")) {
            String[] hostsArray = hosts.split(",");
            for (String host : hostsArray) {
                this.hosts.add(host);
            }
        } else {
            this.hosts.add(hosts);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
