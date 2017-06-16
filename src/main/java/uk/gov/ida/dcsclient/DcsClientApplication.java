package uk.gov.ida.dcsclient;

import com.nimbusds.jose.util.Base64URL;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.gov.ida.dcsclient.config.DcsClientConfiguration;
import uk.gov.ida.dcsclient.resources.EvidenceCheckResource;
import uk.gov.ida.dcsclient.security.DcsDecrypter;
import uk.gov.ida.dcsclient.security.DcsEncrypter;
import uk.gov.ida.dcsclient.security.DcsKeyGenerator;
import uk.gov.ida.dcsclient.security.DcsSecurePayloadExtractor;
import uk.gov.ida.dcsclient.security.DcsSigner;
import uk.gov.ida.dcsclient.security.EvidenceSecurity;
import uk.gov.ida.dcsclient.services.DcsService;

import javax.ws.rs.client.Client;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

public class DcsClientApplication extends Application<DcsClientConfiguration> {
    private final ConfigurationSourceProvider sourceProvider;

    public DcsClientApplication(ConfigurationSourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            new DcsClientApplication(new ResourceConfigurationSourceProvider()).run("server", "dcs-client.yml");
        } else {
            new DcsClientApplication(new FileConfigurationSourceProvider()).run(args);
        }
    }

    @Override
    public void initialize(Bootstrap<DcsClientConfiguration> bootstrap){
        bootstrap.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(
                sourceProvider,
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

        JerseyClientConfiguration httpClientConfiguration = configuration.getHttpClient();

        Client client = new JerseyClientBuilder(environment).using(httpClientConfiguration).build("dcs connection");
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
        RSAPrivateKey clientPrivateSigningKey = DcsKeyGenerator.generatePrivateKey(configuration.getClientPrivateSigningKey());
        X509Certificate clientPublicSigningCert = DcsKeyGenerator.generateCertificate(configuration.getClientSigningCertificate());
        Base64URL clientThumbprint = DcsKeyGenerator.generateThumbprint(clientPublicSigningCert);
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
