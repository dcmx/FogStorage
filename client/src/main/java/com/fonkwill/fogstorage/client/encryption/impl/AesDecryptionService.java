package com.fonkwill.fogstorage.client.encryption.impl;

import com.fonkwill.fogstorage.client.encryption.Decrypter;
import com.fonkwill.fogstorage.client.encryption.Encrypter;
import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;
import com.fonkwill.fogstorage.client.encryption.utils.EncryptionUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AesDecryptionService extends AbstractAesService implements Decrypter  {


    public AesDecryptionService(String key) throws EncryptionException {
        this.key = key;
        byte[] bytesKey = DatatypeConverter.parseHexBinary(this.key);

        this.secretKeySpec = new SecretKeySpec(bytesKey, algorithm);

    }


    @Override
    public byte[] decrypt(byte[] content) throws EncryptionException {
        try {
            Cipher cipher =  Cipher.getInstance(algorithmAndPadding);;
            ByteBuffer byteBuffer = ByteBuffer.wrap(content);
            byte[] iv = new byte[16];
            byteBuffer.get(iv, 0, 16);

            byte[] result = new byte[content.length - 16];
            byteBuffer.get(result, 0, content.length - 16);

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec,  new IvParameterSpec(iv));

            return cipher.doFinal(result);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new EncryptionException("Could not decrypt data", e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionException("No such padding exception", e);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Not such algorithm", e);
        }
    }
}
