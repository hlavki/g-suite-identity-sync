FROM openjdk:15-jdk-slim

ENV KARAF_USER karaf
ENV KARAF_UID 8181
ENV JAVA_MAX_MEM 256m
ENV KARAF_EXEC exec

RUN groupadd -r $KARAF_USER --gid=$KARAF_UID && useradd -rm -g $KARAF_USER --uid=$KARAF_UID $KARAF_USER

COPY --chown=karaf:karaf maven/karaf/ /opt/karaf

# Install karaf distribution
RUN sed -i 's/log4j2.rootLogger.appenderRef.RollingFile/#log4j2.rootLogger.appenderRef.RollingFile/' /opt/karaf/etc/org.ops4j.pax.logging.cfg \
    && sed -i '/felix.fileinstall.log.default/a felix.fileinstall.subdir.mode\ =\ recurse' /opt/karaf/etc/config.properties \
    && sed -i -e '$a log4j2.logger.cxf.name\ =\ org.apache.cxf' /opt/karaf/etc/org.ops4j.pax.logging.cfg \
    && sed -i -e '$a log4j2.logger.cxf.level\ =\ WARN' /opt/karaf/etc/org.ops4j.pax.logging.cfg \
    && mkdir -p /opt/karaf/data /opt/karaf/data/log /opt/karaf/etc/identity \
    && chown -R $KARAF_USER.$KARAF_USER /opt/karaf/data /opt/karaf/data/log /opt/karaf/etc/identity

USER $KARAF_USER

VOLUME ["/opt/karaf/etc/identity"]

EXPOSE 8101 8181

CMD ["/opt/karaf/bin/karaf", "run"]
