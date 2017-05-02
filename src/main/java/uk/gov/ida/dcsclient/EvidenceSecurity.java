package uk.gov.ida.dcsclient;

import com.nimbusds.jose.JOSEException;

import java.security.cert.CertificateEncodingException;

public class EvidenceSecurity {
    private final DcsEncrypter encrypter;
    private final DcsSigner signer;

    public EvidenceSecurity(DcsEncrypter encrypter, DcsSigner signer) {
        this.encrypter = encrypter;
        this.signer = signer;
    }

    public String secure(String payload) throws JOSEException, CertificateEncodingException {
        String signed = signer.sign(payload);
        String encrypted = encrypter.encrypt(signed);
        return signer.sign(encrypted);
    }
}
