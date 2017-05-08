package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.JOSEException;
import org.junit.Test;

import java.security.cert.CertificateEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EvidenceSecurityTest {

    private final DcsEncrypter encrypter = mock(DcsEncrypter.class);
    private final DcsSigner signer = mock(DcsSigner.class);

    @Test
    public void shouldSecurePayload() throws JOSEException, CertificateEncodingException {
        String plaintext = "payload";
        String signedText = "sign1";
        String encryptedText = "encrypted";
        String securedText = "securedPayload";

        when(signer.sign(plaintext)).thenReturn(signedText);
        when(encrypter.encrypt(signedText)).thenReturn(encryptedText);
        when(signer.sign(encryptedText)).thenReturn(securedText);

        String payload = new EvidenceSecurity(encrypter, signer).secure(plaintext);

        assertThat(payload).isEqualTo(securedText);
    }
}