package uk.gov.ida.dcsclient.services;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class DcsService {
    public static final String APPLICATION_MEDIA_TYPE_JOSE = "application/jose";

    private Client httpClient;
    private final String dcsUrl;
    private final String sslRequestHeader;

    public DcsService(Client httpClient, String dcsUrl, String sslRequestHeader){
        this.httpClient = httpClient;
        this.dcsUrl = dcsUrl;
        this.sslRequestHeader = sslRequestHeader;
    }

    public Response call(String encryptedPayload) {
        return httpClient.target(dcsUrl)
                .request()
                .header("X-ssl-client-s-dn", sslRequestHeader)
                .post(Entity.entity(encryptedPayload, APPLICATION_MEDIA_TYPE_JOSE));
    }
}
