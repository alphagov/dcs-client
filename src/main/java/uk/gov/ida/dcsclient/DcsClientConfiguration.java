package uk.gov.ida.dcsclient;

import io.dropwizard.Configuration;

import java.io.File;

public class DcsClientConfiguration extends Configuration {
    private File clientSigningCertificate;
    private File clientPrivateSigningKey;
    private File dcsEncryptionCertificate;
    private String dcsUrl;
    private String sslRequestHeader;

    public File getClientSigningCertificate() {
        return clientSigningCertificate;
    }

    public File getClientPrivateSigningKey() {
        return clientPrivateSigningKey;
    }

    public File getDcsEncryptionCertificate() {
        return dcsEncryptionCertificate;
    }

    public String getDcsUrl() {
        return dcsUrl;
    }

    public String getSslRequestHeader() { return sslRequestHeader; }
}
