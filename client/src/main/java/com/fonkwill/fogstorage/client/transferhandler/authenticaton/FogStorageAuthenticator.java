package com.fonkwill.fogstorage.client.transferhandler.authenticaton;

import com.fonkwill.fogstorage.client.transferhandler.vm.LoginVM;
import com.fonkwill.fogstorage.client.shared.domain.SharedSecret;
import com.fonkwill.fogstorage.client.transferhandler.vm.TokenVM;
import com.fonkwill.fogstorage.client.cryptoservice.exception.EncryptionException;
import com.fonkwill.fogstorage.client.cryptoservice.impl.AbstractAesService;
import com.fonkwill.fogstorage.client.cryptoservice.utils.EncryptionUtils;
import com.fonkwill.fogstorage.client.cryptoservice.CryptoService;
import com.fonkwill.fogstorage.client.controller.FogStorageContext;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class FogStorageAuthenticator implements Authenticator, Interceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";

    private static final Logger logger = LoggerFactory.getLogger(FogStorageAuthenticator.class);

    private String token;

    private SharedSecret sharedSecret;

    private String publicKey;

    private FogStorageContext fogStorageContext;

    private String url;

    private FogStorageAuthenticationService fogStorageAuthenticationService;

    private CryptoService enDeCryptionService;

    private Random random;

    private Instant lastAuthenticated;

    private boolean sharedSecretUsed = false;

    public FogStorageAuthenticator(String url, SharedSecret sharedSecret, String publicKey, FogStorageContext fogStorageContext, CryptoService enDeCryptionService) {
        this.url = url;
        this.sharedSecret = sharedSecret;
        this.publicKey = publicKey;
        this.fogStorageContext = fogStorageContext;
        this.enDeCryptionService = enDeCryptionService;
        this.random = new Random();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.fogStorageAuthenticationService = retrofit.create(FogStorageAuthenticationService.class);
    }


    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        LoginVM loginVM = new LoginVM();
        loginVM.setUsername(fogStorageContext.getUsername());


        //no multiple authentications in parallel
        synchronized (this) {

            //if last authentication is not at least 20 seconds in the past, don't authenticate again
            if (token != null && !lastAuthenticated.isBefore(Instant.now().minus(20, ChronoUnit.SECONDS))){
                return response.request().newBuilder()
                        .removeHeader(AUTHORIZATION_HEADER)
                        .addHeader(AUTHORIZATION_HEADER, BEARER + " " + this.token)
                        .build();
            }
            String randomString = String.valueOf(this.random.nextInt());
            try {
                if (this.sharedSecretUsed) {
                    String sharedSecretString = EncryptionUtils.getKey(AbstractAesService.keyLengthBit, AbstractAesService.algorithm);
                    this.sharedSecret.setValue(sharedSecretString);
                    this.sharedSecretUsed = false;
                }
                String sharedSecret = this.sharedSecret.getValue();
                this.sharedSecretUsed = true;

                String encodedSharedSecret = enDeCryptionService.encryptSharedSecret(sharedSecret, publicKey);
                loginVM.setEncryptedSharedSecret(encodedSharedSecret);

                String encryptedPassword = enDeCryptionService.encryptWithSecret(fogStorageContext.getPassword(), sharedSecret);
                loginVM.setEncryptedPassword(encryptedPassword);

                String encryptedChallenge = enDeCryptionService.encryptWithSecret(randomString, sharedSecret);
                loginVM.setChallenge(encryptedChallenge);
            } catch (EncryptionException e) {
                logger.error("Could not encrypt secret user attributes  - have to stop here");
                return null;
            }

            Call<TokenVM> call = fogStorageAuthenticationService.authenticate(loginVM);

            retrofit2.Response<TokenVM> jwtResponse = call.execute();

            if (jwtResponse.isSuccessful()) {
                TokenVM tokenVM = jwtResponse.body();
                this.token = tokenVM.getToken();

                String challengeResult = tokenVM.getChallengeResult();
                if (!challengeResult.equals(randomString)) {
                    logger.error("Fog Node could not be trusted");
                    return null;
                }


                //everythings fine
                lastAuthenticated = Instant.now();
                return response.request().newBuilder()
                        .removeHeader(AUTHORIZATION_HEADER)
                        .addHeader(AUTHORIZATION_HEADER, BEARER + " " + this.token)
                        .build();
            }
            if (jwtResponse.code() == 403) {
                throw new IOException("Not allowed to access resource");
            }
        }
        return  null;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        if (this.token == null || originalRequest.header(AUTHORIZATION_HEADER) != null) {
            return chain.proceed(originalRequest);
        }

        Request request = originalRequest.newBuilder()
                .removeHeader(AUTHORIZATION_HEADER)
                .addHeader(AUTHORIZATION_HEADER, BEARER+ " "+this.token)
                .build();
        return chain.proceed(request);
    }
}
