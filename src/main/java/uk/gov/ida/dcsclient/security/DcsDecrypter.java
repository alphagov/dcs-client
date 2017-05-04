package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;

import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;

public class DcsDecrypter {
    private RSAPrivateKey privateKey;

    public DcsDecrypter(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String decrypt(String encrypted) throws ParseException, JOSEException {
        JWEObject jweObject = JWEObject.parse(encrypted);
        RSADecrypter rsaDecrypter = new RSADecrypter(privateKey);

        jweObject.decrypt(rsaDecrypter);

        return jweObject.getPayload().toString();
    }
}
