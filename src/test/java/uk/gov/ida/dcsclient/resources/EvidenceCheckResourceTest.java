package uk.gov.ida.dcsclient.resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.dcsclient.DcsService;
import uk.gov.ida.dcsclient.EvidenceSecurity;
import uk.gov.ida.dcsclient.dto.Result;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EvidenceCheckResourceTest {

    private static EvidenceSecurity evidenceSecurity = mock(EvidenceSecurity.class);
    private static DcsService dcsService = mock(DcsService.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new EvidenceCheckResource(evidenceSecurity, dcsService))
            .build();

    @Test
    public void shouldCallDcsServiceWithSecuredPayload() throws Exception {
        String payload = "payload";
        String securedPayload = "secured";
        Result dcsResult = new Result("pass");

        when(evidenceSecurity.secure(payload)).thenReturn(securedPayload);
        when(dcsService.call(securedPayload)).thenReturn(dcsResult);

        Result result = resources.target("/check-evidence")
                .request()
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE), Result.class);

        verify(dcsService).call(securedPayload);
        assertThat(result).isEqualToComparingFieldByField(dcsResult);
    }

}