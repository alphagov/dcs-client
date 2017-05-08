package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.dcsclient.testutils.CertAndKeys;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class DcsDecrypterTest {

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    private final String plainText = "so plain";

    @Before
    public void setUp() throws Exception {
        CertAndKeys certAndKeys = CertAndKeys.generate();
        privateKey = certAndKeys.privateKey;
        publicKey = certAndKeys.publicKey;
    }

    @Test
    public void shouldDecryptInput() throws Exception {
        DcsDecrypter dcsDecrypter = new DcsDecrypter(privateKey);

        JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128CBC_HS256).build();

        JWEObject jweObject = new JWEObject(header, new Payload(plainText));
        RSAEncrypter encrypter = new RSAEncrypter(publicKey);

        jweObject.encrypt(encrypter);
        String encrypted = jweObject.serialize();

        String decrypted = dcsDecrypter.decrypt(encrypted);
        assertThat(decrypted, is(plainText));

    }

    @Test(expected = ParseException.class)
    public void shouldThrowExceptionWhenUnableToParseEncryptedString() throws Exception {
        DcsDecrypter decrypter = new DcsDecrypter(privateKey);
        decrypter.decrypt("not encrypted string");
    }

    @Test(expected = JOSEException.class)
    public void shouldThrowExceptionWhenUnableToDecryptUsingPrivateKey() throws Exception {
        DcsDecrypter decrypter = new DcsDecrypter(mock(RSAPrivateKey.class));

        JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128CBC_HS256).build();

        JWEObject jweObject = new JWEObject(header, new Payload(plainText));
        RSAEncrypter encrypter = new RSAEncrypter(publicKey);

        jweObject.encrypt(encrypter);
        String encrypted = jweObject.serialize();

        decrypter.decrypt(encrypted);
    }
}