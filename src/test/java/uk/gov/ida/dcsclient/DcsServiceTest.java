package uk.gov.ida.dcsclient;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DcsServiceTest {
    @Test
    public void shouldPostJoseToDCS() throws Exception {
        WebTarget target = mock(WebTarget.class);
        Client client = mock(Client.class);
        Response expectedResponse = mock(Response.class);
        Invocation.Builder requestBuilder = mock(Invocation.Builder.class);

        String payload = "some encrypted jose";
        String dcsUrl = "http://dcs";
        String sslRequestHeader = "/CN=ssl two";

        when(client.target(dcsUrl)).thenReturn(target);
        when(target.request()).thenReturn(requestBuilder);
        when(requestBuilder.header(anyString(), anyString())).thenReturn(requestBuilder);
        when(requestBuilder.post(Entity.entity(payload, DcsService.APPLICATION_MEDIA_TYPE_JOSE))).thenReturn(expectedResponse);

        DcsService dcsService = new DcsService(client, dcsUrl, sslRequestHeader);
        Response actualResponse = dcsService.call(payload);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(requestBuilder).header("X-ssl-client-s-dn", sslRequestHeader);
    }
}