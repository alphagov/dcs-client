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

1. You will need to specify some configuration details for the DCS client.  You can do this either by providing a configuration file (see below), or by setting the following environment variables:

	* `CLIENT_SIGNING_KEY`: filepath to private key used for signing
	* `CLIENT_SIGNING_CERT`: filepath to public cert used for signing
	* `CLIENT_ENCRYPTION_KEY`: filepath to private key used for encryption
	* `DCS_ENCRYPTION_CERT`: filepath to DCS's public cert for encryption
	* `KEY_STORE_PATH`: filepath to key store
	* `KEY_STORE_PASSWORD`: password to the key store
	* `TRUST_STORE_PATH`: filepath to the trust store
	* `TRUST_STORE_PASSWORD`: password to the trust store
	* `DCS_URL`: The URL for DCS - currently 
	* `SSL_REQUEST_HEADER`: Distinguished name for SSL handshake
	* `DCS_PORT`: port number for the client (defaults to 11000 if not specified)
	* `DCS_LOG_LEVEL`: application log level (defaults to INFO if not specified)

1. Unzip the distribution zip file, and go to the 'bin' folder.
1. See the sections below for the commands to run the DCS client under various OS environments.
    * If you want to specify a configuration file, add `server pathToConfigFile` at the end of the command, where pathToConfigFile is the full path to a valid yml config file for the DCS client.
1. When the client is running, `POST` the JSON Object to endpoint `/check-evidence` to see DCS

### Creating a configuration file

A template configuration file is provided in each distribution.  You can use this as a basis for a custom configuration file.  In the template, some of the values are set to just use the environment variables described above - these have the format ${VARIABLE_NAME}.  If you prefer to specify these values directly in the file, just replace these variables with the values you want to use.

You can also use the configuration file to control settings used by DropWizard.  For further details on how to do this, see http://www.dropwizard.io/1.1.0/docs/manual/configuration.html.

### Running from Windows

Run the client with `dcs-client.bat`

### Running from a Bash shell

Run the client with `./dcs-client`
