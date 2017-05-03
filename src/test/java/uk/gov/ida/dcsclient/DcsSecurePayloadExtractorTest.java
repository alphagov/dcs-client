package uk.gov.ida.dcsclient;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import org.junit.Test;
import sun.security.rsa.RSAKeyPairGenerator;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DcsSecurePayloadExtractorTest {
    @Test
    public void shouldSignInput() throws Exception {
        KeyPair keyPair = new RSAKeyPairGenerator().generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String plaintext = "Wingardium Leviosa";
        String encrypted = "encrypted";

        RSASSASigner signer = new RSASSASigner(privateKey);

        JWSObject encryptedPayloadJose = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
                new Payload(encrypted)
        );
        JWSObject plaintextPayloadJose = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
                new Payload(plaintext)
        );
        encryptedPayloadJose.sign(signer);
        plaintextPayloadJose.sign(signer);

        DcsDecrypter decryptor = mock(DcsDecrypter.class);
        when(decryptor.decrypt(encrypted)).thenReturn(plaintextPayloadJose.serialize());

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
        KeyPair keyPair = new RSAKeyPairGenerator().generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String encrypted = "encrypted";

        RSASSASigner signer = new RSASSASigner(privateKey);

        JWSObject encryptedPayloadJose = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
                new Payload(encrypted)
        );
        encryptedPayloadJose.sign(signer);

        DcsDecrypter decryptor = mock(DcsDecrypter.class);
        when(decryptor.decrypt(encrypted)).thenThrow(new JOSEException("decryption failed"));

        new DcsSecurePayloadExtractor(decryptor).getPayloadFor(encryptedPayloadJose.serialize());
    }

    @Test(expected = ParseException.class)
    public void shouldThrowExceptionWhenInvalidDecryptedJose() throws Exception {
        KeyPair keyPair = new RSAKeyPairGenerator().generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String encrypted = "encrypted";

        RSASSASigner signer = new RSASSASigner(privateKey);

        JWSObject encryptedPayloadJose = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
                new Payload(encrypted)
        );
        encryptedPayloadJose.sign(signer);

        DcsDecrypter decryptor = mock(DcsDecrypter.class);
        when(decryptor.decrypt(encrypted)).thenReturn("invalid jose");

        new DcsSecurePayloadExtractor(decryptor).getPayloadFor(encryptedPayloadJose.serialize());
    }
}