# dcs-client

[![Build Status](https://travis-ci.org/alphagov/dcs-client.svg?branch=master)](https://travis-ci.org/alphagov/dcs-client)

## Purpose

A client for the Document Checking Service. Agents such as DVLA and DVA should be able to use it to imitate requests sent by IDPs to DCS to test the agents' own services. 

## What it does

The client takes a plain JSON object and produces a JOSE object after signing, encrypting, and again signing the original payload. 

It sends the JOSE to DCS, unsigns, decrypts, and again unsigns the response and provides the end user with the plaintext response from DCS.

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
