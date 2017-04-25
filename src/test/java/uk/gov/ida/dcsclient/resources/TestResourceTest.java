package uk.gov.ida.dcsclient.resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.dcsclient.dto.Result;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestResourceTest {

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TestResource())
            .build();

    @Test
    public void getPassingResult() throws Exception {
        assertThat(resources.target("/test").request().get(Result.class).getStatus())
                .isEqualTo("pass");
    }

}