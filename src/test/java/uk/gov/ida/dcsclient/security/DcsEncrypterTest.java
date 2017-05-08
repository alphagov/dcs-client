package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.dcsclient.testutils.CertAndKeys;

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
    private final String plainText = "so plain";

    @Before
    public void setUp() throws Exception {
        CertAndKeys certAndKeys = CertAndKeys.generate();
        privateKey = certAndKeys.privateKey;
        certification = certAndKeys.certificate;
    }

    @Test
    public void shouldEncryptInput() throws Exception {
        DcsEncrypter dcsEncrypter = new DcsEncrypter(certification);

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

        encrypter.encrypt(plainText);
    }
}