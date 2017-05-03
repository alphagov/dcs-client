package uk.gov.ida.dcsclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {

    private String status;
    private String body;

    //needed for deserialization
    public Result() {

    }

    public Result(String status, String body) {
        this.status = status;
        this.body = body;
    }

    @JsonProperty
    public String getStatus() {
        return this.status;
    }

    @JsonProperty
    public String getBody() { return body; }
}
