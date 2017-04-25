package uk.gov.ida.dcsclient;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class DcsClientApplication extends Application<DcsClientConfiguration> {
    public static void main(String[] args) throws Exception {
        new DcsClientApplication().run(args);
    }

    @Override
    public String getName() {
        return "dcs-client";
    }

    @Override
    public void run(DcsClientConfiguration configuration, Environment environment) throws Exception {

    }
}
