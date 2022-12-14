package com.fonkwill.fogstorage.client.encryption;

import com.fonkwill.fogstorage.client.cryptoservice.exception.EncryptionException;
import com.fonkwill.fogstorage.client.cryptoservice.impl.AesDecryptionService;
import com.fonkwill.fogstorage.client.cryptoservice.impl.AesEncryptionService;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;


@SpringBootTest
public class AesEncryptionTest {


    @Test
    public void testEncryptionDecryption_ShouldEncryptAndDecrypt() {
        String plaintext = "This is a plaintext which is readable for humans and for everyon";

        try {
            AesEncryptionService aesEncryptionService = new AesEncryptionService();
            String secret = aesEncryptionService.getKey();

            byte[] cipher = aesEncryptionService.encrypt(plaintext.getBytes(StandardCharsets.UTF_8));

            AesDecryptionService aesDecryptionService = new AesDecryptionService(secret);

            byte[] plain = aesDecryptionService.decrypt(cipher);

            assertEquals(new String(plain, StandardCharsets.UTF_8), plaintext);

        } catch (EncryptionException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }


    }

}
