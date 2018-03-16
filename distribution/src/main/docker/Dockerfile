FROM openjdk:8-alpine
MAINTAINER Michal Hlavac <miso@hlavki.eu>

ENV KARAF_USER karaf
ENV KARAF_UID 8181
ENV JAVA_MAX_MEM 256m
ENV KARAF_EXEC exec

RUN addgroup -S -g $KARAF_UID $KARAF_USER; \
    adduser -S -u $KARAF_UID -g $KARAF_USER $KARAF_USER

ADD maven/karaf/ /opt/karaf

# Install karaf distribution
RUN sed -i 's/log4j\.rootLogger=INFO,\ out,\ osgi\:\*/log4j\.rootLogger=INFO,\ out,\ stdout,\ osgi\:\*/' /opt/karaf/etc/org.ops4j.pax.logging.cfg \
    && sed -i '/felix.fileinstall.log.default/a felix.fileinstall.subdir.mode\ =\ recurse' /opt/karaf/etc/config.properties \
    && sed -i -e '$a log4j.logger.org.apache.cxf=ERROR' /opt/karaf/etc/org.ops4j.pax.logging.cfg \
    && mkdir -p /opt/karaf/data /opt/karaf/data/log /opt/karaf/etc/identity \
    && chown -R $KARAF_USER.$KARAF_USER /opt/karaf

EXPOSE 8101 8181

VOLUME ["/opt/karaf/etc/identity"]

USER $KARAF_USER

CMD ["/opt/karaf/bin/karaf", "run"]
