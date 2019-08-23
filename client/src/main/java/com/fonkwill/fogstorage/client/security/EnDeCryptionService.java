package com.fonkwill.fogstorage.middleware.shared.security;

import com.fonkwill.fogstorage.middleware.shared.config.ApplicationProperties;
import com.fonkwill.fogstorage.middleware.shared.domain.Placement;
import com.fonkwill.fogstorage.middleware.shared.security.domain.SecretUserAttributes;
import com.fonkwill.fogstorage.middleware.shared.security.domain.TokenDetails;
import com.fonkwill.fogstorage.middleware.shared.security.encryption.Decrypter;
import com.fonkwill.fogstorage.middleware.shared.security.encryption.Encrypter;
import com.fonkwill.fogstorage.middleware.shared.security.encryption.exception.EncryptionException;
import com.fonkwill.fogstorage.middleware.shared.security.encryption.impl.AesEncryptionService;
import com.fonkwill.fogstorage.middleware.shared.security.encryption.impl.RsaDecryptionService;
import com.fonkwill.fogstorage.middleware.shared.service.exceptions.MiddlewareServiceException;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class EnDeCryptionService {

    private ApplicationProperties applicationProperties;

    public SecretUserAttributes decryptSecretUserAttributes(String secretUserAttributeString) throws EncryptionException {
        String privateKey = applicationProperties.getCredentials().getPrivateKey();
        if (privateKey == null) {
            throw new EncryptionException("Could not get private key of this node");
        }
        Decrypter decrypter = new RsaDecryptionService(privateKey);
        byte[] decrypted = decrypter.decrypt(secretUserAttributeString.getBytes(StandardCharsets.UTF_8));

        String decryptedString =  new String(decrypted, StandardCharsets.UTF_8);

        return new Gson().fromJson(decryptedString, SecretUserAttributes.class);

    }

    public String encryptPlacement(Placement placement) throws EncryptionException {
        TokenDetails tokenDetails = SecurityUtils.getCurrentUserTokenDetails();
        if (tokenDetails.getSharedSecret() == null) {
            throw new EncryptionException("Could not get shared Secret for current user");
        }

        Encrypter encrypter = new AesEncryptionService(tokenDetails.getSharedSecret());
        String plainText = new Gson().toJson(placement);

        byte[] encrypted = encrypter.encrypt(plainText.getBytes(StandardCharsets.UTF_8));

        return new String(encrypted, StandardCharsets.UTF_8);
    }



}
