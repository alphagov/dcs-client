# Contributing

This document describes development related task in dcs-client.

The only pre-requisite we have is Java 8. Gradle is already bundled in the repository.

## Running the tests

    ./pre-commit.sh

## Running the client

    ./startup.sh

## Creating and running executable JAR

Creating:

    ./gradlew clean build shadowJar

Running:

    java -jar dcs-client.jar server configuration/dcs-client.yml

Note that the 'server' argument, which allows you to specify a configuration file, is optional.  If you do not provide it, then values will be used from the environment variables instead.


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
