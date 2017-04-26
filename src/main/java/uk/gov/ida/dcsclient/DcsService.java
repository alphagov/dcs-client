package uk.gov.ida.dcsclient;

import org.eclipse.jetty.http.HttpStatus;
import uk.gov.ida.dcsclient.dto.Result;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class DcsService {
    public static final String APPLICATION_MEDIA_TYPE_JOSE = "application/jose";

    private Client httpClient;
    private final String dcsUrl;

    public DcsService(Client httpClient, String dcsUrl){
        this.httpClient = httpClient;
        this.dcsUrl = dcsUrl;
    }

    public Result call(String encryptedPayload) {
        Response response = httpClient.target(dcsUrl)
                .request()
                .post(Entity.entity(encryptedPayload, APPLICATION_MEDIA_TYPE_JOSE));

        return response.getStatus() == HttpStatus.OK_200
                ? new Result("pass")
                : new Result("fail");
    }
}
