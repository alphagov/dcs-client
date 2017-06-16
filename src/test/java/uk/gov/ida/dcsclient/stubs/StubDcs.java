package uk.gov.ida.dcsclient.stubs;

import com.nimbusds.jose.util.Base64URL;
import uk.gov.ida.dcsclient.security.*;

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

@Path("/checks/driving-licence")
@Produces(MediaType.APPLICATION_JSON)
public class StubDcs {

    private EvidenceSecurity evidenceSecurity;
    private DcsSecurePayloadExtractor securePayloadExtractor;

    @POST
    public String licenceCheck(String evidencePayload) throws Exception {
        String payloadFor = securePayloadExtractor.getPayloadFor(evidencePayload);
        return evidenceSecurity.secure("you sent me: " + payloadFor);
    }

    public void setUpKeys(String certificatePath, String privateKeyPath) throws InvalidKeySpecException, CertificateException, NoSuchAlgorithmException, IOException {
        X509Certificate certificate = DcsKeyGenerator.generateCertificate(new File(certificatePath));
        RSAPrivateKey privateKey = DcsKeyGenerator.generatePrivateKey(new File(privateKeyPath));
        Base64URL thumbprint = DcsKeyGenerator.generateThumbprint(certificate);

        DcsEncrypter encrypter = new DcsEncrypter(certificate);
        DcsSigner signer = new DcsSigner(privateKey, thumbprint);
        DcsDecrypter decrypter = new DcsDecrypter(privateKey);

        this.evidenceSecurity = new EvidenceSecurity(encrypter, signer);
        this.securePayloadExtractor = new DcsSecurePayloadExtractor(decrypter);
    }
}
