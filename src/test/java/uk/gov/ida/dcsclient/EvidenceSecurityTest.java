package uk.gov.ida.dcsclient;

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
        EvidenceSecurity evidenceSecurity = new EvidenceSecurity(encrypter, signer);
        when(signer.sign("payload")).thenReturn("sign1");
        when(encrypter.encrypt("sign1")).thenReturn("encrypted");
        when(signer.sign("encrypted")).thenReturn("securedPayload");

        String payload = evidenceSecurity.secure("payload");
        assertThat(payload).isEqualTo("securedPayload");
    }
}