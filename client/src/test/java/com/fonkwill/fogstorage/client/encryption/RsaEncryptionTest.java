package com.fonkwill.fogstorage.client.encryption;

import com.fonkwill.fogstorage.client.cryptoservice.exception.EncryptionException;
import com.fonkwill.fogstorage.client.cryptoservice.impl.RsaDecryptionService;
import com.fonkwill.fogstorage.client.cryptoservice.impl.RsaEncryptionService;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;


@SpringBootTest
public class RsaEncryptionTest {



    private static final String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJXvICmXilGci64IRmIvv6P/eVI+bp9SneU424r3dQirvwqWOkqWwRW8Dl3ny3eL8d67xPCBXQcIs+nifb9AT2+ONp4AgiznhWrSWPxAeo1hVYr3TqmnvRuNI/kPjzemfbm9LEYca4+udl743x03wCY7qKO3wy81O45nM3heAb9BAgMBAAECgYBa0fh3eTXt7ZemmZiYzTeWjZ/ds7eqho7iVKiX6wFXeUFFlm5E1ECte7ZwJ4JWLc0bED5hONsy8ZCZsfpdqQT0LpTtdxxISCeY6ov5H86vvE+m/RxOKBCk9GdRnzht6ZDBGGPIKIdnaBUPtazc0k2Yrh2+naPOQSEus+0p/XBRSQJBAMiF1oqDKxuDZGCfXQh0Fq/yvL6ipuMF5wjSiMlEbBW9nE9pVORS+v4oS9SbUMKpf8HT8Gi6XpzpevooeQDY578CQQC/ak69wj0OuhBV6aDLMst8mdfZzaov1glD5Nr75lfWWnDQ2Ku530qE6koD5tF61of2arEykTSLKz4zC1imBxj/AkEAj+y4S1zQUQQixNM9lDa2nf0tCet2u1XeOM/cDBIHPieFDN5+Zw9ERa4ol2YPNlkwcK+tS44RILDYarsn9rq4wwJACGikIuPoiMAVvaBwxtxpYgTkHZZSVp2hdJEWKaC51RnOAO6zxIuRm3srWzaFl07bVDDlreIBG+f/aICkGU8QgwJAA7p6bQHkPg8XyujMGO+714xWAEgjhUQJOXolGX0pOlKKoFEvSZyp5bTiUkf9Aj3EO7av7rnUpT91soPbtJoi5w==";

    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCV7yApl4pRnIuuCEZiL7+j/3lSPm6fUp3lONuK93UIq78KljpKlsEVvA5d58t3i/Heu8TwgV0HCLPp4n2/QE9vjjaeAIIs54Vq0lj8QHqNYVWK906pp70bjSP5D483pn25vSxGHGuPrnZe+N8dN8AmO6ijt8MvNTuOZzN4XgG/QQIDAQAB";



    @Test
    public void testEncryptionDecryption_ShouldEncryptAndDecrypt() {
        String plaintext = "This is a plaintext which is readable for humans and for everyon";

        try {
            RsaEncryptionService rsaEncryptionService = new RsaEncryptionService(publicKey);


            byte[] cipher = rsaEncryptionService.encrypt(plaintext.getBytes(StandardCharsets.UTF_8));

            RsaDecryptionService rsaDecryptionService = new RsaDecryptionService(privateKey);

            byte[] plain = rsaDecryptionService.decrypt(cipher);

            assertEquals(new String(plain, StandardCharsets.UTF_8), plaintext);

        } catch (EncryptionException e) {
            e.printStackTrace();
            fail();

        }


    }

}
