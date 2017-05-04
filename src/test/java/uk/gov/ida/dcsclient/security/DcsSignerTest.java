package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.Base64URL;
import org.junit.Before;
import org.junit.Test;
import sun.security.rsa.RSAKeyPairGenerator;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DcsSignerTest {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private Base64URL thumbprint = new Base64URL("test thumbprint");

    @Before
    public void setUp() {
        RSAKeyPairGenerator keyGen = new RSAKeyPairGenerator();
        KeyPair keyPair = keyGen.generateKeyPair();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    @Test
    public void shouldSignInput() throws Exception {
        DcsSigner dcsSigner = new DcsSigner(privateKey, thumbprint);

        String signed = dcsSigner.sign("some payload");
        JWSObject parsed = JWSObject.parse(signed);
        boolean verified = parsed.verify(new RSASSAVerifier(publicKey));

        assertThat(verified).isTrue();
        assertThat(parsed.getHeader().getX509CertThumbprint()).isEqualTo(thumbprint);
        assertThat(parsed.getPayload().toString()).isEqualTo("some payload");
    }

    @Test(expected = JOSEException.class)
    public void shouldThrowExceptionWhenUnableToSign() throws Exception {
        RSAPrivateKey mockPrivateKey = mock(RSAPrivateKey.class);

        DcsSigner dcsSigner = new DcsSigner(mockPrivateKey, null);

        dcsSigner.sign("some payload");
    }
}