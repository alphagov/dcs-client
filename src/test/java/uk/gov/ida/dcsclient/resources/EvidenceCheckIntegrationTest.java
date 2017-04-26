package uk.gov.ida.dcsclient.resources;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.nimbusds.jose.util.Base64URL;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.*;
import sun.security.rsa.RSAKeyPairGenerator;
import uk.gov.ida.dcsclient.DcsEncrypter;
import uk.gov.ida.dcsclient.DcsService;
import uk.gov.ida.dcsclient.DcsSigner;
import uk.gov.ida.dcsclient.EvidenceSecurity;
import uk.gov.ida.dcsclient.dto.Result;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EvidenceCheckIntegrationTest {

    private static EvidenceSecurity evidenceSecurity;
    private static DcsService dcsService;
    private static final String dcsUrl = "http://localhost:8089/dcscheck";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @BeforeClass
    public static void setUp(){
        RSAKeyPairGenerator keyGen = new RSAKeyPairGenerator();
        KeyPair keyPair = keyGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        Base64URL thumbprint = new Base64URL("thumbprint");

        DcsEncrypter encrypter = new DcsEncrypter(publicKey);
        DcsSigner signer = new DcsSigner(privateKey, thumbprint);
        evidenceSecurity = new EvidenceSecurity(encrypter, signer);

        Client client = JerseyClientBuilder.createClient();
        dcsService = new DcsService(client, dcsUrl);
    }

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new EvidenceCheckResource(evidenceSecurity, dcsService))
            .build();

    @Test
    public void shouldPassIfDcsReturns200() throws Exception {
        stubFor(post(urlEqualTo("/dcscheck"))
                .withHeader("Content-Type", equalTo("application/jose"))
                .willReturn(aResponse()
                        .withStatus(200)));

        String payload = "payload";
        String securedPayload = evidenceSecurity.secure(payload);

        Result result = resources.target("/check-evidence")
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE), Result.class);

        assertThat(result.getStatus()).isEqualTo("pass");
//        verify(postRequestedFor(urlMatching("/dcscheck"))
//                .withRequestBody(matching(securedPayload)));
    }

}