package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.JOSEException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.CertificateEncodingException;

public class EvidenceSecurity {
    private final DcsEncrypter encrypter;
    private final DcsSigner signer;
    private static final Logger LOG = LoggerFactory.getLogger(EvidenceSecurity.class);

    public EvidenceSecurity(DcsEncrypter encrypter, DcsSigner signer) {
        this.encrypter = encrypter;
        this.signer = signer;
    }

    public String secure(String payload) throws JOSEException, CertificateEncodingException {
        LOG.info("Securing request payload: {}", payload);
        String signed = signer.sign(payload);
        String encrypted = encrypter.encrypt(signed);
        return signer.sign(encrypted);
    }
}
