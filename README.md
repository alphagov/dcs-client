# dcs-client

[![Build Status](https://travis-ci.org/alphagov/dcs-client.svg?branch=master)](https://travis-ci.org/alphagov/dcs-client)

## Purpose

A testing client for the Document Checking Service. Agents such as DVLA and DVA should be able to use it to imitate requests sent by IDPs to DCS to test the agents' own services, for performing end-to-end testing.

## What it does

The client takes a plain JSON object and produces a JOSE object after signing, encrypting, and again signing the original payload. 

It sends the JOSE to DCS, unsigns, decrypts, and again unsigns the response and provides the end user with the plaintext response from DCS.

## Where to find the latest release

https://github.com/alphagov/dcs-client/releases/latest

## Client usage

Unzip the distribution zip file and run the client.

You can run configure the client either by providing a configuration file or by setting environment variables. The sections below explain how to configure and run the client with these different configuration styles.

### Configuration file

Here's an example of a configuration file:

    server:
      applicationConnectors:
        - type: http
          port: ${DCS_PORT:-11000}
    
    httpClient:
      timeout: 15s
      connectionTimeout: 15s
      cookiesEnabled: false
      connectionTimeout: 15s
      tls:
        keyStorePath: ${KEY_STORE_PATH}
        keyStorePassword: ${KEY_STORE_PASSWORD}
        trustStorePath: ${TRUST_STORE_PATH}
        trustStorePassword: ${TRUST_STORE_PASSWORD}
        verifyHostname: false
    
    logging:
      level: ${DCS_LOG_LEVEL:-INFO}
      loggers:
        "uk.gov": DEBUG
      appenders:
        - type: console
    
    dcsUrl: ${DCS_URL}
    
    clientPrivateSigningKey: ${CLIENT_SIGNING_KEY}
    clientSigningCertificate: ${CLIENT_SIGNING_CERT}
    clientPrivateEncryptionKey: ${CLIENT_ENCRYPTION_KEY}
    
    dcsEncryptionCertificate: ${DCS_ENCRYPTION_CERT}

In the example above, some of the values are set to just use the environment variables described in the next section - these have the format ${VARIABLE_NAME}.  If you prefer to specify these values directly in the file, just replace these variables with the values you want to use.

The configuration file includes configuration for DropWizard, a Java web framework. The `server`, `httpClient` and `logging` sections of the configuration file relate to DropWizard-specific settings. For further details on how to configure them, see http://www.dropwizard.io/1.1.0/docs/manual/configuration.html.


To run the client with a configuration file in Linux:
    
    ./dcs-client/bin/dcs-client server <config-file>
    
To run the client with a configuration file in Windows:
    
    dcs-client\bin\dcs-client.bat server <config-file>

### Environment variables

If you do not specify a configuration file, you must set the following environment variables:

* `CLIENT_SIGNING_KEY`: filepath to private key used for signing
* `CLIENT_SIGNING_CERT`: filepath to public cert used for signing
* `CLIENT_ENCRYPTION_KEY`: filepath to private key used for encryption
* `DCS_ENCRYPTION_CERT`: filepath to DCS's public cert for encryption
* `KEY_STORE_PATH`: filepath to key store
* `KEY_STORE_PASSWORD`: password to the key store
* `TRUST_STORE_PATH`: filepath to the trust store
* `TRUST_STORE_PASSWORD`: password to the trust store
* `DCS_URL`: The URL for DCS
* `SSL_REQUEST_HEADER`: Distinguished name for SSL handshake
* `DCS_PORT`: port number for the client (defaults to 11000 if not specified)
* `DCS_LOG_LEVEL`: application log level (defaults to INFO if not specified)


To run the client in Linux:
    
    ./dcs-client/bin/dcs-client
    
To run the client in Windows:
    
    dcs-client\bin\dcs-client.bat

Note that startup.sh will set the environment variables values for dcs-client to run locally.

### Sending a request to the client

    curl -X POST -d @client-request-data.json http://localhost:11000/check-evidence

The `client-request-data.json` should contain the appropriate request for the check you want to test (i.e. either a licence or a passport check).
See https://github.com/alphagov/dcs-client/blob/master/src/test/resources/request.json as an example.

## Development and contributing

See https://github.com/alphagov/dcs-client/blob/master/CONTRIBUTING.md for more information.
