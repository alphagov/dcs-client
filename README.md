# dcs-client

[![Build Status](https://travis-ci.org/alphagov/dcs-client.svg?branch=master)](https://travis-ci.org/alphagov/dcs-client)

## Purpose

A client for the Document Checking Service. Agents such as DVLA and DVA should be able to use it to imitate requests sent by IDPs to DCS to test the agents' own services. 

## What it does

The client takes a plain JSON object and produces a JOSE object after signing, encrypting, and again signing the original payload. 

It sends the JOSE to DCS, unsigns, decrypts, and again unsigns the response and provides the end user with the plaintext response from DCS.

## Where to find the latest release

https://github.com/alphagov/dcs-client/releases/latest

## Creating deployable JAR

`./gradlew clean build shadowJar`

## Running the client

1. Have a running instance of DCS somewhere (local or remote)
1. Make sure the `dcsUrl` is set to point to this DCS instance in `<env>-dcs-client.yml`
1. Make sure you have the following environment variables set:

	* `CLIENT_SIGNING_KEY`: filepath to private key used for signing
	* `CLIENT_SIGNING_CERT`: filepath to public cert used for signing
	* `CLIENT_ENCRYPTION_KEY`: filepath to private key used for encryption
	* `DCS_ENCRYPTION_CERT`: filepath to DCS's public cert for encryption
	* `KEY_STORE_PATH`: filepath to key store
	* `KEY_STORE_PASSWORD`: password to the key store
	* `TRUST_STORE_PATH`: filepath to the trust store
	* `TRUST_STORE_PASSWORD`: password to the trust store

1. Run the client with `./startup.sh`
1. `POST` the JSON Object to endpoint `/check-evidence` to see DCS

## Running the tests

`./pre-commit.sh`

## Creating a new release

1. Make sure the build.gradle file has been updated with the correct version number for the release.
1. Run the following command: `./gradlew -PapiToken <your GitHub API token>`

If you don't have a GitHub API token, you will need to create one:

1. Go to your GitHub personal settings.
1. Look at the Developer Settings box at the bottom left of the page.  Click 'Personal Access Tokens'.
1. Choose 'Generate new token'.
1. Provide a name of your choice for the token in the description field.  Make sure the 'repo' scopes are selected.
1. Click 'Generate token' to complete the process, and now copy the token and copy the token from the green box.  Record it somewhere safe, as GitHub will nto show it to you again (but if you lose it, you can just delete the token and generate a new one).
