package uk.gov.ida.dcsclient.stubs;

import com.nimbusds.jose.util.Base64URL;
import uk.gov.ida.dcsclient.security.*;

import javax.crypto.KeyGenerator;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;

import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;
import static org.apache.commons.codec.digest.DigestUtils.sha1;

@Path("/checks/driving-licence")
@Produces(MediaType.APPLICATION_JSON)
public class StubDcs {

    private EvidenceSecurity evidenceSecurity;
    private DcsSecurePayloadExtractor securePayloadExtractor;

    @POST
    public String licenceCheck(String evidencePayload) throws Exception {
        String payloadFor = securePayloadExtractor.getPayloadFor(evidencePayload);
        String securedResponse = evidenceSecurity.secure("you sent me: " + payloadFor);
        return securedResponse;
    }

    public void setUpKeys(String certificatePath, String privateKeyPath) throws InvalidKeySpecException, CertificateException, NoSuchAlgorithmException, IOException {
        X509Certificate certificate = new DcsKeyGenerator().generateCertificate(new File(certificatePath));
        RSAPrivateKey privateKey = new DcsKeyGenerator().generatePrivateKey(new File(privateKeyPath));
        Base64URL thumbprint = new Base64URL(encodeBase64URLSafeString(sha1(certificate.getEncoded())));

        DcsEncrypter encrypter = new DcsEncrypter(certificate);
        DcsSigner signer = new DcsSigner(privateKey, thumbprint);
        DcsDecrypter decrypter = new DcsDecrypter(privateKey);

        this.evidenceSecurity = new EvidenceSecurity(encrypter, signer);
        this.securePayloadExtractor = new DcsSecurePayloadExtractor(decrypter);
    }
}
