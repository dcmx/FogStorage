package com.fonkwill.fogstorage.client.encryption.impl;

import com.fonkwill.fogstorage.client.encryption.Encrypter;
import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;
import com.fonkwill.fogstorage.client.encryption.utils.EncryptionUtils;
import com.fonkwill.fogstorage.client.encryption.utils.SecureRandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class AesEncryptionService  extends AbstractAesService implements Encrypter {

    private SecureRandom secureRandom;

    private static Logger logger = LoggerFactory.getLogger(AesEncryptionService.class);

    //private byte[] iv;

    private Random random;

    public AesEncryptionService(String key) throws EncryptionException {
        this.key = key;
        this.random = new Random();
     //   logger.debug("Going to instantiate secure Random");
      //  this.secureRandom = new SecureRandom();
      //  logger.debug("Secure Random instantiated");
       // random.setSeed(secureRandom.nextLong());
        //this.iv = new byte[16];
        //this.secureRandom.nextBytes(iv);
        init();
    }


    public AesEncryptionService() throws EncryptionException {
        this.key = EncryptionUtils.getKey(keyLengthBit, algorithm);
      //  this.random = new Random();
     //   this.secureRandom = new SecureRandom();
      //  logger.debug("Secure Random instantiated");
      //  random.setSeed(secureRandom.nextLong());
        init();

    }

    @Override
    public byte[] encrypt(byte[] content) throws EncryptionException {
        try {
         //   logger.info("Starting encrypt {}", this.toString());
            Cipher cipher =  Cipher.getInstance(algorithmAndPadding);;
          //  logger.info("Cipher instantiated {}", this.toString());
            byte[] iv =  SecureRandomUtil.getBytes(16);

//            /**/logger.info("Got new random {}", this.toString());
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec );
//            logger.info("Cipher inited {}", this.toString());

            byte[] result = cipher.doFinal(content);
//            logger.info("Result encrypted {}", this.toString());
            byte[] ivAdded = new byte[result.length + 16];
            ByteBuffer byteBuffer = ByteBuffer.wrap(ivAdded);
            byteBuffer.put(ivParameterSpec.getIV());
            byteBuffer.put(result);
//            logger.info("Result added to buffer {}", this.toString());
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

    private void init() {
        byte[] bytesKey = DatatypeConverter.parseHexBinary(this.key);
        this.secretKeySpec = new SecretKeySpec(bytesKey, algorithm);
    }


}
