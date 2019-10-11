package com.fonkwill.fogstorage.client.cryptoservice;

import com.fonkwill.fogstorage.client.shared.domain.Placement;
import com.fonkwill.fogstorage.client.cryptoservice.exception.EncryptionException;
import com.fonkwill.fogstorage.client.cryptoservice.impl.AesDecryptionService;
import com.fonkwill.fogstorage.client.cryptoservice.impl.AesEncryptionService;
import com.fonkwill.fogstorage.client.cryptoservice.impl.RsaEncryptionService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class CryptoService {

    public Placement decryptPlacement(String placementString, String key) throws EncryptionException {

        Decrypter decrypter = new AesDecryptionService(key);
        byte[] decrypted = decrypter.decrypt(Base64.getDecoder().decode(placementString));

        try {
            Placement placement = new Gson().fromJson(new String(decrypted, StandardCharsets.UTF_8), Placement.class);
            return  placement;
        } catch (JsonSyntaxException e) {
            throw new EncryptionException("Could not decrypt placement - key obviously wrong");
        }

    }


    public String encryptSharedSecret(String sharedSecret, String publicKey) throws EncryptionException {
        Encrypter encrypter = new RsaEncryptionService(publicKey);
        byte[] encryptedSharedSecret =  encrypter.encrypt(sharedSecret.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedSharedSecret);

    }

    public String encryptWithSecret(String password, String sharedSecret) throws EncryptionException {
        Encrypter encrypter = new AesEncryptionService(sharedSecret);
        byte[] encryptedPassword = encrypter.encrypt(password.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedPassword);
    }

    public String encryptPlacement(Placement placement, String sharedSecret) throws EncryptionException {
        Encrypter encrypter = new AesEncryptionService(sharedSecret);
        String plainText = new Gson().toJson(placement);

        byte[] encrypted = encrypter.encrypt(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encrypted);

    }
}
