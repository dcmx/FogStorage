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
import java.security.SecureRandom;
import java.util.Base64;

public class AesEncryptionService  extends AbstractAesService implements Encrypter {


    public AesEncryptionService() throws EncryptionException {
        this.key = EncryptionUtils.getKey(keyLengthBit, algorithm);
        byte[] bytesKey = DatatypeConverter.parseHexBinary(this.key);

        this.secretKeySpec = new SecretKeySpec(bytesKey, algorithm);
        try {
            this.cipher = Cipher.getInstance(algorithmAndPadding);

        } catch (NoSuchAlgorithmException  | NoSuchPaddingException  e) {
            throw new EncryptionException("Could not create EncryptionService", e);
        }
    }

    @Override
    public byte[] encrypt(byte[] content) throws EncryptionException {
        try {
            Cipher cipher =  Cipher.getInstance(algorithmAndPadding);;
            IvParameterSpec ivParameterSpec = new IvParameterSpec(SecureRandom.getSeed(16));
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec );

            byte[] result = cipher.doFinal(content);
            byte[] ivAdded = new byte[result.length + 16];
            ByteBuffer byteBuffer = ByteBuffer.wrap(ivAdded);
            byteBuffer.put(ivParameterSpec.getIV());
            byteBuffer.put(result);
            return byteBuffer.array();

        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new EncryptionException("Could not encrypt data", e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionException("No such padding", e);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("No such algorithm", e);
        }
    }

    public String getKey() {
        return key;
    }


}
