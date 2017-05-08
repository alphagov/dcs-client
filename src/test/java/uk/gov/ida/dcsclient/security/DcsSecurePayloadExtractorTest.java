package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.dcsclient.testutils.CertAndKeys;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DcsSecurePayloadExtractorTest {

    private final String encryptedString = "Protego";
    private final String plaintext = "Alohamora";
    private RSASSASigner signer;

    @Before
    public void setUp() throws Exception {
        CertAndKeys certAndKeys = CertAndKeys.generate();
        signer = new RSASSASigner(certAndKeys.privateKey);
    }

    @Test
    public void shouldExtractPlaintextPayloadFromSecuredResponse() throws Exception {
        JWSObject encryptedPayloadJose = sign(encryptedString);
        JWSObject plaintextPayloadJose = sign(plaintext);

        DcsDecrypter decryptor = mock(DcsDecrypter.class);
        when(decryptor.decrypt(encryptedString)).thenReturn(plaintextPayloadJose.serialize());

        String result = new DcsSecurePayloadExtractor(decryptor).getPayloadFor(encryptedPayloadJose.serialize());

        assertThat(result).isEqualTo(plaintext);
    }

    @Test(expected = ParseException.class)
    public void shouldThrowExceptionWhenInvalidEncryptedJose() throws Exception {
        DcsDecrypter decryptor = mock(DcsDecrypter.class);
        when(decryptor.decrypt(anyString())).thenThrow(new RuntimeException("Shouldn't have called decrypt :/"));
        new DcsSecurePayloadExtractor(decryptor).getPayloadFor("some invalid jose");
    }

    @Test(expected = JOSEException.class)
    public void shouldThrowExceptionWhenDecryptFails() throws Exception {
        JWSObject encryptedPayloadJose = sign(encryptedString);

        DcsDecrypter decryptor = mock(DcsDecrypter.class);
        when(decryptor.decrypt(encryptedString)).thenThrow(new JOSEException("decryption failed"));

        new DcsSecurePayloadExtractor(decryptor).getPayloadFor(encryptedPayloadJose.serialize());
    }

    @Test(expected = ParseException.class)
    public void shouldThrowExceptionWhenInvalidDecryptedJose() throws Exception {
        JWSObject encryptedPayloadJose = sign(encryptedString);

        DcsDecrypter decryptor = mock(DcsDecrypter.class);
        when(decryptor.decrypt(encryptedString)).thenReturn("invalid jose");

        new DcsSecurePayloadExtractor(decryptor).getPayloadFor(encryptedPayloadJose.serialize());
    }

    private JWSObject sign(String payload) throws JOSEException {
        JWSObject jose = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
                new Payload(payload)
        );
        jose.sign(signer);
        return jose;
    }
}