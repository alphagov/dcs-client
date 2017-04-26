package uk.gov.ida.dcsclient;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.util.Base64URL;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.security.interfaces.RSAPublicKey;

import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;
import static org.apache.commons.codec.digest.DigestUtils.sha1;

public class DcsEncrypter {
    private RSAPublicKey publicKey;

    public DcsEncrypter(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String encrypt(String input) throws JOSEException {
        Base64URL thumbprint = new Base64URL(encodeBase64URLSafeString(sha1(publicKey.getEncoded())));

        JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128CBC_HS256)
                .x509CertThumbprint(thumbprint)
                .build();

        JWEObject jweObject = new JWEObject(header, new Payload(input));
        RSAEncrypter encrypter = new RSAEncrypter(publicKey);

        jweObject.encrypt(encrypter);
        return jweObject.serialize();
    }
}
