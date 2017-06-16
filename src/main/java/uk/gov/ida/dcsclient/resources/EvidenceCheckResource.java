package uk.gov.ida.dcsclient.resources;

import com.nimbusds.jose.JOSEException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.dcsclient.dto.Result;
import uk.gov.ida.dcsclient.security.DcsSecurePayloadExtractor;
import uk.gov.ida.dcsclient.security.EvidenceSecurity;
import uk.gov.ida.dcsclient.services.DcsConnectionException;
import uk.gov.ida.dcsclient.services.DcsService;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.cert.CertificateEncodingException;
import java.text.ParseException;

@Path("/check-evidence")
@Produces(MediaType.APPLICATION_JSON)
public class EvidenceCheckResource {

    static final String DCS_ERROR_HEADER = "X-Dcs-Error";
    private static final Logger LOG = LoggerFactory.getLogger("uk.gov");

    private EvidenceSecurity evidenceSecurity;
    private DcsService dcsService;
    private DcsSecurePayloadExtractor securePayloadExtractor;

    public EvidenceCheckResource(EvidenceSecurity evidenceSecurity, DcsService dcsService, DcsSecurePayloadExtractor securePayloadExtractor) {
        this.evidenceSecurity = evidenceSecurity;
        this.dcsService = dcsService;
        this.securePayloadExtractor = securePayloadExtractor;
    }

    @POST
    public Response checkEvidence(String evidencePayload) throws ParseException, JOSEException, CertificateEncodingException {
        try {
            String securedPayload = evidenceSecurity.secure(evidencePayload);
            Response response = dcsService.call(securedPayload);

            String body = getBodyFrom(response);
            String error = getErrorFrom(response);

            Result result = new Result(response.getStatus(), body, error);
            return Response.status(response.getStatus()).entity(result).build();
        } catch (DcsConnectionException e) {
            LOG.warn(ExceptionUtils.getStackTrace(e));
            return Response.status(HttpStatus.SERVICE_UNAVAILABLE_503).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    private String getErrorFrom(Response response) {
        Object errorHeader = response.getHeaders().getFirst(DCS_ERROR_HEADER);
        return errorHeader == null ? "" : errorHeader.toString();
    }

    private String getBodyFrom(Response response) throws ParseException, JOSEException {
        String responseBody = response.readEntity(String.class);
        LOG.info("Received response from DCS: ", responseBody);
        return responseBody.isEmpty()
                ? responseBody
                : securePayloadExtractor.getPayloadFor(responseBody);
    }
}
