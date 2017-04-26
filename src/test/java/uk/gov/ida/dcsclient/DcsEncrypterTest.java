package uk.gov.ida.dcsclient;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.util.Base64URL;
import org.junit.Before;
import org.junit.Test;
import sun.security.rsa.RSAKeyPairGenerator;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DcsEncrypterTest {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    @Before
    public void setUp() {
        RSAKeyPairGenerator keyGen = new RSAKeyPairGenerator();
        KeyPair keyPair = keyGen.generateKeyPair();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    @Test
    public void shouldEncryptInput() throws Exception {
        DcsEncrypter dcsEncrypter = new DcsEncrypter(publicKey);

        String plainText = "so plain";
        String encrypted = dcsEncrypter.encrypt(plainText);

        JWEObject jweObject = JWEObject.parse(encrypted);
        RSADecrypter rsaDecrypter = new RSADecrypter(privateKey);

        jweObject.decrypt(rsaDecrypter);

        assertThat(jweObject.getPayload().toString(), is(plainText));

    }

    @Test(expected = JOSEException.class)
    public void shouldThrowExceptionWhenUnableToEncrypt() throws Exception {
        RSAPublicKey mockPublicKey = mock(RSAPublicKey.class);
        when(mockPublicKey.getEncoded()).thenReturn(new byte[] {});

        DcsEncrypter encrypter = new DcsEncrypter(mockPublicKey);

        encrypter.encrypt("some payload");
    }
}