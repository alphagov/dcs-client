package uk.gov.ida.dcsclient.services;

import org.junit.Test;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DcsServiceTest {

    private final String payload = "some encrypted jose";
    private final String dcsUrl = "http://dcs";
    private final String sslRequestHeader = "/CN=ssl two";

    @Test
    public void shouldPostJoseToDCS() throws Exception {
        WebTarget target = mock(WebTarget.class);
        Client client = mock(Client.class);
        Response expectedResponse = mock(Response.class);
        Invocation.Builder requestBuilder = mock(Invocation.Builder.class);

        when(client.target(dcsUrl)).thenReturn(target);
        when(target.request()).thenReturn(requestBuilder);
        when(requestBuilder.header(anyString(), anyString())).thenReturn(requestBuilder);
        when(requestBuilder.post(Entity.entity(payload, DcsService.APPLICATION_MEDIA_TYPE_JOSE))).thenReturn(expectedResponse);

        DcsService dcsService = new DcsService(client, dcsUrl, sslRequestHeader);
        Response actualResponse = dcsService.call(payload);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(requestBuilder).header("X-ssl-client-s-dn", sslRequestHeader);
    }

    @Test(expected = DcsConnectionException.class)
    public void shouldThrowExceptionIfConnectionToDcsFailed() throws DcsConnectionException {
        WebTarget target = mock(WebTarget.class);
        Client client = mock(Client.class);

        when(client.target(dcsUrl)).thenReturn(target);
        when(target.request()).thenThrow(new ProcessingException("something died"));

        DcsService dcsService = new DcsService(client, dcsUrl, sslRequestHeader);
        dcsService.call(payload);
    }
}