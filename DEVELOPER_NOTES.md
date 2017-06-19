# dcs-client

[![Build Status](https://travis-ci.org/alphagov/dcs-client.svg?branch=master)](https://travis-ci.org/alphagov/dcs-client)

## Running the client

1. Have a running instance of DCS somewhere (local or remote)
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
1. `POST` the JSON Object to endpoint `/check-evidence` to see DCS

### Running from the JAR

    java -jar dcs-client.jar server configuration/dcs-client.yml

Note that the 'server' argument, which allows you to specify a configuration file, is optional.  If you do not provide it, then values will be used from the environment variables instead.

### Running with gradle

Run the client with `./startup.sh server configuration/dcs-client.yml`

Note that the server argument is optional (see previous section).

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
