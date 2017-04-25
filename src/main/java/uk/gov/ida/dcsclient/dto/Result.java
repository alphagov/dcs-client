package uk.gov.ida.dcsclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {

    private String status;

    //needed for deserialization
    public Result() {

    }

    public Result(String status) {
        this.status = status;
    }

    @JsonProperty
    public String getStatus() {
        return this.status;
    }

}
