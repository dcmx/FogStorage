package com.fonkwill.fogstorage.client.client.vm;


/**
 * View Model object for storing a user's credentials.
 */
public class LoginVM {

    private String username;

    private Boolean rememberMe;

    private String encryptedSharedSecret;

    private String encryptedPassword;

    private String challenge;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }


    public void setEncryptedSharedSecret(String encryptedSharedSecret) {
        this.encryptedSharedSecret = encryptedSharedSecret;
    }


    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
    @Override
    public String toString() {
        return "LoginVM{" + "password='*****'" + ", username='" + username + '\'' + ", rememberMe=" + rememberMe + '}';
    }


    public String getEncryptedSharedSecret() {
        return encryptedSharedSecret;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }
}


