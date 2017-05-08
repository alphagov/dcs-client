package uk.gov.ida.dcsclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {

    private int status;
    private String body;
    private String error;

    //needed for deserialization
    public Result() {}

    public Result(int status, String body, String error) {
        this.status = status;
        this.body = body;
        this.error = error;
    }

    @JsonProperty
    public int getStatus() {
        return this.status;
    }

    @JsonProperty
    public String getBody() { return body; }

    @JsonProperty
    public String getError() { return error; }
}
