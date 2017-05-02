package uk.gov.ida.dcsclient;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import org.junit.Before;
import org.junit.Test;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DcsEncrypterTest {

    private RSAPrivateKey privateKey;
    private X509Certificate certification;
    public static final long ONE_YEAR = (long) 365 * 24 * 60 * 60;

    @Before
    public void setUp() throws Exception {
        CertAndKeyGen certGen = new CertAndKeyGen("RSA", "SHA256WithRSA", null);
        certGen.generate(2048);

        X509Certificate cert = certGen.getSelfCertificate(new X500Name("CN=My Application,O=My Organisation,L=My City,C=DE"), ONE_YEAR);

        privateKey = (RSAPrivateKey) certGen.getPrivateKey();
        certification = cert;
    }

    @Test
    public void shouldEncryptInput() throws Exception {
        DcsEncrypter dcsEncrypter = new DcsEncrypter(certification);

        String plainText = "so plain";
        String encrypted = dcsEncrypter.encrypt(plainText);

        JWEObject jweObject = JWEObject.parse(encrypted);
        RSADecrypter rsaDecrypter = new RSADecrypter(privateKey);

        jweObject.decrypt(rsaDecrypter);

        assertThat(jweObject.getPayload().toString(), is(plainText));

    }

    @Test(expected = JOSEException.class)
    public void shouldThrowExceptionWhenUnableToEncrypt() throws Exception {
        X509Certificate mockPublicKey = mock(X509Certificate.class);
        when(mockPublicKey.getPublicKey()).thenReturn(mock(RSAPublicKey.class));
        when(mockPublicKey.getEncoded()).thenReturn(new byte[] {});

        DcsEncrypter encrypter = new DcsEncrypter(mockPublicKey);

        encrypter.encrypt("some payload");
    }
}