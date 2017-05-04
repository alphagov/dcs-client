package uk.gov.ida.dcsclient.resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.dcsclient.security.DcsSecurePayloadExtractor;
import uk.gov.ida.dcsclient.services.DcsService;
import uk.gov.ida.dcsclient.security.EvidenceSecurity;
import uk.gov.ida.dcsclient.dto.Result;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EvidenceCheckResourceTest {

    private static EvidenceSecurity evidenceSecurity = mock(EvidenceSecurity.class);
    private static DcsService dcsService = mock(DcsService.class);
    private static DcsSecurePayloadExtractor securePayloadExtractor = mock(DcsSecurePayloadExtractor.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new EvidenceCheckResource(evidenceSecurity, dcsService, securePayloadExtractor))
            .build();

    @Test
    public void shouldCallDcsServiceWithSecuredPayload() throws Exception {
        String payload = "payload";
        String securedPayload = "secured";
        String dcsRawResponse = "encrypted body";
        String dcsDecryptedResponse = "body";

        Result expectedResult = new Result("200", dcsDecryptedResponse);

        Response mockResponse = mock(Response.class);
        when(mockResponse.readEntity(String.class)).thenReturn(dcsRawResponse);
        when(mockResponse.getStatus()).thenReturn(200);

        when(evidenceSecurity.secure(payload)).thenReturn(securedPayload);
        when(dcsService.call(securedPayload)).thenReturn(mockResponse);
        when(securePayloadExtractor.getPayloadFor(dcsRawResponse)).thenReturn(dcsDecryptedResponse);

        Result result = resources.target("/check-evidence")
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE), Result.class);

        assertThat(result).isEqualToComparingFieldByField(expectedResult);
    }

}