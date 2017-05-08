package uk.gov.ida.dcsclient;

import com.nimbusds.jose.util.Base64URL;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.client.JerseyClientBuilder;
import uk.gov.ida.dcsclient.config.DcsClientConfiguration;
import uk.gov.ida.dcsclient.resources.EvidenceCheckResource;
import uk.gov.ida.dcsclient.security.*;
import uk.gov.ida.dcsclient.services.DcsService;

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
        EvidenceSecurity evidenceSecurity = createEvidenceSecurity(configuration);
        DcsSecurePayloadExtractor securePayloadExtractor = createSecurePayloadExtractor(configuration);

        Client client = JerseyClientBuilder.createClient();
        DcsService dcsService = new DcsService(client, configuration.getDcsUrl(), configuration.getSslRequestHeader());

        environment.jersey().register(new EvidenceCheckResource(evidenceSecurity, dcsService, securePayloadExtractor));
    }

    private DcsSecurePayloadExtractor createSecurePayloadExtractor(DcsClientConfiguration configuration) throws Exception {
        DcsDecrypter dcsDecrypter = createDecrypter(configuration);
        return new DcsSecurePayloadExtractor(dcsDecrypter);
    }

    private EvidenceSecurity createEvidenceSecurity(DcsClientConfiguration configuration) throws Exception {
        DcsEncrypter encrypter = createEncrypter(configuration);
        DcsSigner signer = createSigner(configuration);
        return new EvidenceSecurity(encrypter, signer);
    }

    private DcsSigner createSigner(DcsClientConfiguration configuration) throws Exception {
        X509Certificate clientPublicSigningCert = DcsKeyGenerator.generateCertificate(configuration.getClientSigningCertificate());
        RSAPrivateKey clientPrivateSigningKey = DcsKeyGenerator.generatePrivateKey(configuration.getClientPrivateSigningKey());
        Base64URL clientThumbprint = new Base64URL(encodeBase64URLSafeString(sha1(clientPublicSigningCert.getEncoded())));
        return new DcsSigner(clientPrivateSigningKey, clientThumbprint);
    }

    private DcsEncrypter createEncrypter(DcsClientConfiguration configuration) throws Exception {
        X509Certificate dcsPublicEncryptionCert = DcsKeyGenerator.generateCertificate(configuration.getDcsEncryptionCertificate());
        return new DcsEncrypter(dcsPublicEncryptionCert);
    }

    private DcsDecrypter createDecrypter(DcsClientConfiguration configuration) throws Exception {
        RSAPrivateKey clientPrivateEncryptionKey = DcsKeyGenerator.generatePrivateKey(configuration.getClientPrivateEncryptionKey());
        return new DcsDecrypter(clientPrivateEncryptionKey);
    }
}
