package uk.gov.ida.dcsclient.resources;

import uk.gov.ida.dcsclient.security.DcsSecurePayloadExtractor;
import uk.gov.ida.dcsclient.services.DcsService;
import uk.gov.ida.dcsclient.security.EvidenceSecurity;
import uk.gov.ida.dcsclient.dto.Result;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/check-evidence")
@Produces(MediaType.APPLICATION_JSON)
public class EvidenceCheckResource {

    private EvidenceSecurity evidenceSecurity;
    private DcsService dcsService;
    private DcsSecurePayloadExtractor securePayloadExtractor;

    public EvidenceCheckResource(EvidenceSecurity evidenceSecurity, DcsService dcsService, DcsSecurePayloadExtractor securePayloadExtractor) {
        this.evidenceSecurity = evidenceSecurity;
        this.dcsService = dcsService;
        this.securePayloadExtractor = securePayloadExtractor;
    }

    @POST
    public Result checkEvidence(String evidencePayload) throws Exception {
        String securedPayload = evidenceSecurity.secure(evidencePayload);
        Response response = dcsService.call(securedPayload);
        String body = securePayloadExtractor.getPayloadFor(response.readEntity(String.class));
        String status = String.valueOf(response.getStatus());
        return new Result(status, body);
    }
}
