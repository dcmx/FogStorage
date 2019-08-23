package com.fonkwill.fogstorage.client.client.vm;

public class TokenVM {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChallengeResult() {
        return challengeResult;
    }

    public void setChallengeResult(String challengeResult) {
        this.challengeResult = challengeResult;
    }

    private String challengeResult;
}
