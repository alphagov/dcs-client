package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.util.Base64URL;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

public class DcsEncrypter {
    private X509Certificate certificate;

    public DcsEncrypter(X509Certificate certificate) {
        this.certificate = certificate;
    }

    String encrypt(String input) throws JOSEException, CertificateEncodingException {
        RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();

        Base64URL thumbprint = DcsKeyGenerator.generateThumbprint(certificate);

        JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128CBC_HS256)
                .x509CertThumbprint(thumbprint)
                .build();

        JWEObject jweObject = new JWEObject(header, new Payload(input));
        RSAEncrypter encrypter = new RSAEncrypter(publicKey);

        jweObject.encrypt(encrypter);
        return jweObject.serialize();
    }
}
