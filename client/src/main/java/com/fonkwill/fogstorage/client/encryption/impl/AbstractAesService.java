package com.fonkwill.fogstorage.client.encryption.impl;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AbstractAesService {

    protected static final int keyLengthBit = 128;
    protected static final String algorithm = "AES";
    protected static final String algorithmAndPadding = "AES/CBC/PKCS5Padding";

    protected Cipher cipher;

    protected String key;

    protected SecretKeySpec secretKeySpec;

}
