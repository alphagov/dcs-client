package uk.gov.ida.dcsclient.resources;

import uk.gov.ida.dcsclient.dto.Result;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {

    @GET
    public Result getPassingResult() {
        return new Result("pass");
    }
}
