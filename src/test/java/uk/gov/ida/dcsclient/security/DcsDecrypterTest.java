package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import org.junit.Before;
import org.junit.Test;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class DcsDecrypterTest {

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public static final long ONE_YEAR = (long) 365 * 24 * 60 * 60;

    @Before
    public void setUp() throws Exception {
        CertAndKeyGen certGen = new CertAndKeyGen("RSA", "SHA256WithRSA", null);
        certGen.generate(2048);

        X509Certificate cert = certGen.getSelfCertificate(new X500Name("CN=My Application,O=My Organisation,L=My City,C=DE"), ONE_YEAR);

        privateKey = (RSAPrivateKey) certGen.getPrivateKey();
        publicKey = (RSAPublicKey) cert.getPublicKey();
    }

    @Test
    public void shouldDecryptInput() throws Exception {
        DcsDecrypter dcsDecrypter = new DcsDecrypter(privateKey);

        String plainText = "so plain";

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

        JWEObject jweObject = new JWEObject(header, new Payload("so plain"));
        RSAEncrypter encrypter = new RSAEncrypter(publicKey);

        jweObject.encrypt(encrypter);
        String encrypted = jweObject.serialize();

        decrypter.decrypt(encrypted);
    }
}