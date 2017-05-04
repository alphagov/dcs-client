package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;

import java.text.ParseException;

public class DcsSecurePayloadExtractor {
    private DcsDecrypter decryptor;

    public DcsSecurePayloadExtractor(DcsDecrypter decryptor) {
        this.decryptor = decryptor;
    }

    public String getPayloadFor(String jose) throws ParseException, JOSEException {
        String encryptedPayload = extractPayloadFromSerialisedJose(jose);
        String signedPayload = decryptor.decrypt(encryptedPayload);
        return extractPayloadFromSerialisedJose(signedPayload);
    }

    private String extractPayloadFromSerialisedJose(String jose) throws ParseException {
        return JWSObject.parse(jose).getPayload().toString();
    }
}
