package uk.gov.ida.dcsclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class SampleLicenceRequest {
    private final String surname = "DEFAULT";
    private final String forenames = "GUY";
    private final String dateOfBirth = "1948-05-08";
    private final String issueDate = "1980-01-01";
    private final String expiryDate = "2999-01-01";
    private final String licenceNumber = "DEFAU405088NUMBR";
    private final String issueNumber = "10";
    private final String postcode = "SW1A 2AA";
    private final String issuingAuthority = "DVLA";
    private final String correlationId = "DEFAULT-CORRELATION-ID";
    private final String requestId = "DEFAULT-REQUEST-ID";
    private final String clientId = "DEFAULT-DCS-CLIENT-ID";
    private final String timestamp = ISODateTimeFormat.dateTime().print(DateTime.now());

    @JsonProperty
    public String getSurname() {
        return surname;
    }

    @JsonProperty
    public String getForenames() {
        return forenames;
    }

    @JsonProperty
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @JsonProperty
    public String getIssueDate() {
        return issueDate;
    }

    @JsonProperty
    public String getExpiryDate() {
        return expiryDate;
    }

    @JsonProperty
    public String getLicenceNumber() {
        return licenceNumber;
    }

    @JsonProperty
    public String getIssueNumber() {
        return issueNumber;
    }

    @JsonProperty
    public String getPostcode() {
        return postcode;
    }

    @JsonProperty
    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    @JsonProperty
    public String getCorrelationId() {
        return correlationId;
    }

    @JsonProperty
    public String getRequestId() {
        return requestId;
    }

    @JsonProperty
    public String getClientId() {
        return clientId;
    }

    @JsonProperty
    public String getTimestamp() {
        return timestamp;
    }
}
