package uk.gov.ida.dcsclient.resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.dcsclient.dto.Result;
import uk.gov.ida.dcsclient.security.DcsSecurePayloadExtractor;
import uk.gov.ida.dcsclient.security.EvidenceSecurity;
import uk.gov.ida.dcsclient.services.DcsService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static uk.gov.ida.dcsclient.resources.EvidenceCheckResource.DCS_ERROR_HEADER;

public class EvidenceCheckResourceTest {

    private static EvidenceSecurity evidenceSecurity = mock(EvidenceSecurity.class);
    private static DcsService dcsService = mock(DcsService.class);
    private static DcsSecurePayloadExtractor securePayloadExtractor = mock(DcsSecurePayloadExtractor.class);
    private final Response mockDcsResponse = mock(Response.class);

    private final String payload = "payload";
    private final String securedPayload = "secured payload";
    private final String dcsRawResponse = "dcs encrypted body";
    private final String expectedDecryptedBody = "dcs response body";

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new EvidenceCheckResource(evidenceSecurity, dcsService, securePayloadExtractor))
            .build();

    @Before
    public void setUp(){
        reset(evidenceSecurity, dcsService, securePayloadExtractor, mockDcsResponse);
    }

    @Test
    public void shouldCallDcsServiceWithSecuredPayload() throws Exception {
        when(mockDcsResponse.readEntity(String.class)).thenReturn(dcsRawResponse);
        when(mockDcsResponse.getHeaders()).thenReturn(new MultivaluedHashMap<>());
        when(mockDcsResponse.getStatus()).thenReturn(200);

        when(evidenceSecurity.secure(payload)).thenReturn(securedPayload);
        when(dcsService.call(securedPayload)).thenReturn(mockDcsResponse);
        when(securePayloadExtractor.getPayloadFor(dcsRawResponse)).thenReturn(expectedDecryptedBody);

        Result expectedResult = new Result(200, expectedDecryptedBody, "");

        Response response = resources.target("/check-evidence")
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE));

        Result result = response.readEntity(Result.class);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(result).isEqualToComparingFieldByField(expectedResult);
    }

    @Test
    public void shouldNotExtractSecuredResponseFromDcsIfBodyIsEmpty() throws Exception {
        when(mockDcsResponse.readEntity(String.class)).thenReturn("");
        when(mockDcsResponse.getHeaders()).thenReturn(new MultivaluedHashMap<>());
        when(mockDcsResponse.getStatus()).thenReturn(503);

        when(evidenceSecurity.secure(payload)).thenReturn(securedPayload);
        when(dcsService.call(securedPayload)).thenReturn(mockDcsResponse);

        Result expectedResult = new Result(503, "", "");

        Response response = resources.target("/check-evidence")
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE));

        Result result = response.readEntity(Result.class);

        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(result).isEqualToComparingFieldByField(expectedResult);
        verify(securePayloadExtractor, never()).getPayloadFor(anyString());
    }

    @Test
    public void shouldReturnResultWithErrorHeadersFromDcs() throws Exception {
        String dcsError = "Internal Server Error";
        MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add(DCS_ERROR_HEADER, dcsError);

        when(mockDcsResponse.readEntity(String.class)).thenReturn("");
        when(mockDcsResponse.getHeaders()).thenReturn(headers);
        when(mockDcsResponse.getStatus()).thenReturn(500);

        when(evidenceSecurity.secure(payload)).thenReturn(securedPayload);
        when(dcsService.call(securedPayload)).thenReturn(mockDcsResponse);

        Result expectedResult = new Result(500, "", dcsError);

        Response response = resources.target("/check-evidence")
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE));

        Result result = response.readEntity(Result.class);

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(result).isEqualToComparingFieldByField(expectedResult);
        verify(securePayloadExtractor, never()).getPayloadFor(anyString());
    }
}