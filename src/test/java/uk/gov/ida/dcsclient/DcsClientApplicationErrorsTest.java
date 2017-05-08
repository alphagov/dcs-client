package uk.gov.ida.dcsclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.ida.dcsclient.config.DcsClientConfiguration;
import uk.gov.ida.dcsclient.testutils.DcsClientApplicationTestBase;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class DcsClientApplicationErrorsTest extends DcsClientApplicationTestBase {

    @Rule
    public final DropwizardAppRule<DcsClientConfiguration> dcsClientApplication =
            new DropwizardAppRule<>(
                    DcsClientApplication.class,
                    ResourceHelpers.resourceFilePath("test-dcs-client.yml"),
                    ConfigOverride.config("dcsUrl", "http://localhost:3000/checks/driving-licence"));

    @Test
    public void checkEvidenceShouldReturnAppropriateMessageWhenDCSUrlNotReachable() throws JsonProcessingException {
        Client client = new JerseyClientBuilder().build();

        Response response = client.target(
                String.format("http://localhost:%d/check-evidence", dcsClientApplication.getLocalPort()))
                .request()
                .post(Entity.json("hello world"));

        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(response.readEntity(String.class)).isEqualTo("Cannot connect to provided DCS URL");
    }
}
