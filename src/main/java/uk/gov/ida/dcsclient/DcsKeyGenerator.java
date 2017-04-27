package uk.gov.ida.dcsclient;

import com.google.common.io.Files;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class DcsKeyGenerator {

    private static final String algorithm = "RSA";

    public static RSAPublicKey generatePublicKey(File publicCertificate) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, CertificateException {
        java.security.cert.CertificateFactory certificateFactory = java.security
                .cert
                .CertificateFactory
                .getInstance("X.509");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Files.toByteArray(publicCertificate));
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);
        return (RSAPublicKey) certificate.getPublicKey();
    }

    public static RSAPrivateKey generatePrivateKey(File privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Files.toByteArray(privateKey)));
    }
}
