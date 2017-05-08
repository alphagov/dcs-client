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

    public static final String DCS_ERROR_HEADER = "X-Dcs-Error";
    public static final Logger LOG = LoggerFactory.getLogger("uk.gov");

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

            String responseBody = response.readEntity(String.class);
            String body = responseBody.isEmpty()
                    ? responseBody
                    : securePayloadExtractor.getPayloadFor(responseBody);

            Object errorHeader = response.getHeaders().getFirst(DCS_ERROR_HEADER);
            String error = errorHeader == null ? "" : errorHeader.toString();

            return Response.ok(new Result(response.getStatus(), body, error)).build();
        } catch (DcsConnectionException e) {
            LOG.warn(ExceptionUtils.getStackTrace(e));
            return Response.status(HttpStatus.SERVICE_UNAVAILABLE_503).entity(e.getMessage()).build();
        }
    }
}
