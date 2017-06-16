package uk.gov.ida.dcsclient.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64URL;

import java.security.interfaces.RSAPrivateKey;

public class DcsSigner {

    private final Base64URL thumbprint;
    private final RSAPrivateKey privateKey;

    public DcsSigner(RSAPrivateKey privateKey, Base64URL thumbprint) {
        this.privateKey = privateKey;
        this.thumbprint = thumbprint;
    }

    String sign(String input) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .x509CertThumbprint(thumbprint)
                .build();

        JWSObject jwsObject = new JWSObject(header, new Payload(input));

        JWSSigner signer = new RSASSASigner(privateKey);
        jwsObject.sign(signer);

        return jwsObject.serialize();
    }
}
