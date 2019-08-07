package com.fonkwill.fogstorage.client.encryption;

import com.fonkwill.fogstorage.client.encryption.exception.EncryptionException;

public interface Encrypter {

    /**
     * Encrypts a byte array with the given key and returns a byte array of the same length
     * @param content The byte array to encrypt
     * @return An byte array with the encrypted data
     */
    public byte[] encrypt(byte[] content) throws EncryptionException;
}
