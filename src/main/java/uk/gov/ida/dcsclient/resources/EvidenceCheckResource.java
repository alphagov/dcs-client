package uk.gov.ida.dcsclient.resources;

import uk.gov.ida.dcsclient.DcsService;
import uk.gov.ida.dcsclient.EvidenceSecurity;
import uk.gov.ida.dcsclient.dto.Result;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/check-evidence")
@Produces(MediaType.APPLICATION_JSON)
public class EvidenceCheckResource {

    private EvidenceSecurity evidenceSecurity;
    private DcsService dcsService;

    public EvidenceCheckResource(EvidenceSecurity evidenceSecurity, DcsService dcsService) {
        this.evidenceSecurity = evidenceSecurity;
        this.dcsService = dcsService;
    }

    @POST
    public Result checkEvidence(String evidencePayload) throws Exception {
        String securedPayload = evidenceSecurity.secure(evidencePayload);
        return dcsService.call(securedPayload);
    }
}
