FROM alpine:3.5

LABEL maintainer verify-tech@digital.cabinet-office.gov.uk

ARG VERSION
ENV VERSION ${VERSION}
ENV JAR_NAME dcs-client-${VERSION}-all.jar
ENV INSTALL_PATH /usr/local/lib/${JAR_NAME}

RUN apk update
RUN apk upgrade
RUN apk add openjdk8-jre-base
RUN apk add --update openjdk8

COPY build/libs/${JAR_NAME} /usr/local/lib
COPY configuration/dcs-client.yml /etc
CMD java -jar ${INSTALL_PATH} server /etc/dcs-client.yml

EXPOSE 11000 11001

