package uk.gov.ida.dcsclient;

import com.nimbusds.jose.util.Base64URL;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.client.JerseyClientBuilder;
import uk.gov.ida.dcsclient.resources.EvidenceCheckResource;

import javax.ws.rs.client.Client;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;
import static org.apache.commons.codec.digest.DigestUtils.sha1;

public class DcsClientApplication extends Application<DcsClientConfiguration> {
    public static void main(String[] args) throws Exception {
        new DcsClientApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<DcsClientConfiguration> bootstrap){
        bootstrap.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor()
            )
        );
    }

    @Override
    public String getName() {
        return "dcs-client";
    }

    @Override
    public void run(DcsClientConfiguration configuration, Environment environment) throws Exception {
        X509Certificate dcsPublicEncryptionCert = DcsKeyGenerator.generateCertificate(configuration.getDcsEncryptionCertificate());
        X509Certificate clientPublicSigningCert = DcsKeyGenerator.generateCertificate(configuration.getClientSigningCertificate());
        RSAPrivateKey clientPrivateSigningKey = DcsKeyGenerator.generatePrivateKey(configuration.getClientPrivateSigningKey());
        RSAPrivateKey clientPrivateEncryptionKey = DcsKeyGenerator.generatePrivateKey(configuration.getClientPrivateEncryptionKey());

        Base64URL clientThumbprint = new Base64URL(encodeBase64URLSafeString(sha1(clientPublicSigningCert.getEncoded())));

        DcsEncrypter encrypter = new DcsEncrypter(dcsPublicEncryptionCert);
        DcsSigner signer = new DcsSigner(clientPrivateSigningKey, clientThumbprint);
        EvidenceSecurity evidenceSecurity = new EvidenceSecurity(encrypter, signer);

        Client client = JerseyClientBuilder.createClient();
        DcsService dcsService = new DcsService(client, configuration.getDcsUrl(), configuration.getSslRequestHeader());

        DcsDecrypter dcsDecrypter = new DcsDecrypter(clientPrivateEncryptionKey);
        DcsSecurePayloadExtractor securePayloadExtractor = new DcsSecurePayloadExtractor(dcsDecrypter);

        environment.jersey().register(new EvidenceCheckResource(evidenceSecurity, dcsService, securePayloadExtractor));
    }
}
