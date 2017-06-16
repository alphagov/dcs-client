package uk.gov.ida.dcsclient.services;

public class DcsConnectionException extends Exception {

    private static final String MESSAGE = "Cannot connect to provided DCS URL";

    public DcsConnectionException(Exception exception) {
        super(MESSAGE, exception);
    }
}
