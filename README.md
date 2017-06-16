# dcs-client

[![Build Status](https://travis-ci.org/alphagov/dcs-client.svg?branch=master)](https://travis-ci.org/alphagov/dcs-client)

## Purpose

A client for the Document Checking Service. Agents such as DVLA and DVA should be able to use it to imitate requests sent by IDPs to DCS to test the agents' own services. 

## What it does

The client takes a plain JSON object and produces a JOSE object after signing, encrypting, and again signing the original payload. 

It sends the JOSE to DCS, unsigns, decrypts, and again unsigns the response and provides the end user with the plaintext response from DCS.

## Where to find the latest release

https://github.com/alphagov/dcs-client/releases/latest

## Running the client

1. Have a running instance of DCS somewhere (local or remote)
1. Make sure you have the following environment variables set:

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

1. See 'running from the JAR' or 'running with gradle' section below, as appropriate.
1. `POST` the JSON Object to endpoint `/check-evidence` to see DCS

### Running from the JAR

    java -jar dcs-client.jar server configuration/dcs-client.yml

### Running with gradle

Run the client with `./startup.sh`

## Running the tests

`./pre-commit.sh`

## Creating deployable JAR

`./gradlew clean build shadowJar`

## Creating a new release

Note that gradle will automatically run the pre-commit tests and create a deployable JAR when you follow the process below; you do not need to do these things separately.

1. Edit the gradle.build file and make sure the correct values are used for the following variables (just after the section where plugins are applied):
    1. version: The correct version number for the release.
    1. apiToken: Your GitHub API token (see below)
1. Run the following command: `./gradlew githubRelease

If you don't have a GitHub API token, you will need to create one:

1. Go to your GitHub personal settings.
1. Look at the Developer Settings box at the bottom left of the page.  Click 'Personal Access Tokens'.
1. Choose 'Generate new token'.
1. Provide a name of your choice for the token in the description field.  Make sure the 'repo' scopes are selected.
1. Click 'Generate token' to complete the process, and now copy the token and copy the token from the green box.  Record it somewhere safe, as GitHub will nto show it to you again (but if you lose it, you can just delete the token and generate a new one).
