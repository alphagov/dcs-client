package uk.gov.ida.dcsclient;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.dcsclient.dto.Result;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DcsServiceTest {

    private final Client client = mock(Client.class);
    private final Response response = mock(Response.class);
    private final Invocation.Builder requestBuilder = mock(Invocation.Builder.class);
    private final String payload = "some encrypted jose";
    private final String dcsUrl = "http://dcs";
    private final String sslRequestHeader = "ssl two";

    @Before
    public void setup() {
        WebTarget target = mock(WebTarget.class);

        when(client.target(dcsUrl)).thenReturn(target);
        when(target.request()).thenReturn(requestBuilder);
        when(requestBuilder.header(anyString(), anyString())).thenReturn(requestBuilder);
        when(requestBuilder.post(Entity.entity(payload, DcsService.APPLICATION_MEDIA_TYPE_JOSE))).thenReturn(response);
    }

    @Test
    public void shouldPostJoseToDCS() throws Exception {
        when(response.getStatus()).thenReturn(HttpStatus.OK_200);

        DcsService dcsService = new DcsService(client, dcsUrl, sslRequestHeader);
        Result result = dcsService.call(payload);

        assertThat(result.getStatus()).isEqualTo("pass");
        verify(requestBuilder).header("X-ssl-client-s-dn", sslRequestHeader);
    }

    @Test
    public void shouldReturnFailedResultIfFailedResponse() throws Exception {
        when(response.getStatus()).thenReturn(HttpStatus.BAD_REQUEST_400);

        DcsService dcsService = new DcsService(client, dcsUrl, sslRequestHeader);
        Result result = dcsService.call(payload);

        assertThat(result.getStatus()).isEqualTo("fail");
    }

}