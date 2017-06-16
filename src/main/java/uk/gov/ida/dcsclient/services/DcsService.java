package uk.gov.ida.dcsclient.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class DcsService {
    public static final String APPLICATION_MEDIA_TYPE_JOSE = "application/jose";
    public static final String COMMON_NAME_HEADER = "X-ssl-client-s-dn";
    private static final Logger LOG = LoggerFactory.getLogger(DcsService.class);;

    private final Client httpClient;
    private final String dcsUrl;
    private final String sslRequestHeader;

    public DcsService(Client httpClient, String dcsUrl, String sslRequestHeader){
        this.httpClient = httpClient;
        this.dcsUrl = dcsUrl;
        this.sslRequestHeader = sslRequestHeader;
    }

    public Response call(String encryptedPayload) throws DcsConnectionException {
        LOG.info("Sending secured request to DCS");
        try {
            return httpClient.target(dcsUrl)
                    .request()
                    .header(COMMON_NAME_HEADER, sslRequestHeader)
                    .post(Entity.entity(encryptedPayload, APPLICATION_MEDIA_TYPE_JOSE));
        } catch (ProcessingException e) {
            throw new DcsConnectionException(e);
        }
    }
}
