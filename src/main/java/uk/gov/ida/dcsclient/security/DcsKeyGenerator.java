package uk.gov.ida.dcsclient.security;

import com.google.common.io.Files;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import static com.google.common.base.Charsets.US_ASCII;

public class DcsKeyGenerator {

    private static final String algorithm = "RSA";

    public static X509Certificate generateCertificate(File publicCertificate) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, CertificateException {
        java.security.cert.CertificateFactory certificateFactory = java.security
                .cert
                .CertificateFactory
                .getInstance("X.509");

        String certString = Files.toString(publicCertificate, US_ASCII);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(certString.getBytes(StandardCharsets.UTF_8));
        return (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);
    }

    public static RSAPrivateKey generatePrivateKey(File privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Files.toByteArray(privateKey)));
    }
}
