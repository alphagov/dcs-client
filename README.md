# dcs-client

[![Build Status](https://travis-ci.org/alphagov/dcs-client.svg?branch=master)](https://travis-ci.org/alphagov/dcs-client)

## Purpose

A client for the Document Checking Service. Agents such as DVLA and DVA should be able to use it to imitate requests sent by IDPs to DCS to test the agents' own services. 

## What it does

The client takes a plain JSON object and produces a JOSE object after signing, encrypting, and again signing the original payload. 

It sends the JOSE to DCS, unsigns, decrypts, and again unsigns the response and provides the end user with the plaintext response from DCS.

## Where to find the latest release

https://github.com/alphagov/dcs-client/releases/latest

## Using the client

In development mode, you can run the client either with `gradle` or by producing an executable JAR.

This section explains how to setup the client and make a request.

### Prerequisites

1. Have a running instance of DCS. You can start up one locally or use an existing DCS environment.
1. Unless you specify a configuration file as a commandline argument when running the client, you will need to configure the following environment variables:

	* `CLIENT_SIGNING_KEY`: filepath to private key used for signing
	* `CLIENT_SIGNING_CERT`: filepath to public cert used for signing
	* `CLIENT_ENCRYPTION_KEY`: filepath to private key used for encryption
	* `DCS_ENCRYPTION_CERT`: filepath to DCS's public cert for encryption
	* `KEY_STORE_PATH`: filepath to key store
	* `KEY_STORE_PASSWORD`: password to the key store
	* `TRUST_STORE_PATH`: filepath to the trust store
	* `TRUST_STORE_PASSWORD`: password to the trust store
	* `DCS_URL`: The url that you want to send the request to
	* `SSL_REQUEST_HEADER`: Distinguished name for SSL handshake
	* `DCS_PORT`: port number for the client (defaults to 11000 if not specified)
	* `DCS_LOG_LEVEL`: application log level (defaults to INFO if not specified)

1. See 'running from the JAR' or 'running with gradle' section below, as appropriate.

### Running with gradle

Run the client with `./startup.sh`

### Creating and running executable JAR

Creating:

    ./gradlew clean build shadowJar

Running:

    java -jar dcs-client.jar server configuration/dcs-client.yml

Note that the 'server' argument, which allows you to specify a configuration file, is optional.  If you do not provide it, then values will be used from the environment variables instead.

### Sending a request to the client

    curl -X POST -d @client-request-data.json http://localhost:11000/check-evidence

The `client-request-data.json` should contain the appropriate request for the check you want to test (i.e. either a licence or a passport check).
See https://github.com/alphagov/dcs-client/blob/master/src/test/resources/request.json as an example.

## Running the tests

    ./pre-commit.sh

## Creating a new release

Note that gradle will automatically run the pre-commit tests and create a deployable JAR when you follow the process below; you do not need to do these things separately.

1. Edit gradle.properties file and check that the version number information is correct.
1. Run the following command: `./gradlew -PgithubApiToken=yourApiToken release`, where yourApiToken is your own GitHub API token.

If you don't have a GitHub API token, you will need to create one:

1. Go to your GitHub personal settings.
1. Look at the Developer Settings box at the bottom left of the page.  Click 'Personal Access Tokens'.
1. Choose 'Generate new token'.
1. Provide a name of your choice for the token in the description field.  Make sure the 'repo' scopes are selected.
1. Click 'Generate token' to complete the process, and now copy the token and copy the token from the green box.  Record it somewhere safe, as GitHub will nto show it to you again (but if you lose it, you can just delete the token and generate a new one).
