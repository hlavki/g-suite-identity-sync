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
    && echo -e "org.osgi.service.http.secure.enabled=true\norg.ops4j.pax.web.ssl.keystore=\${karaf.etc}/keystore/account-manager.jks\norg.ops4j.pax.web.ssl.password=changeit\norg.ops4j.pax.web.ssl.keypassword=changeit" >> /opt/karaf/etc/org.ops4j.pax.web.cfg \
    && sed -i -e '$a log4j.logger.org.apache.cxf.interceptor.LoggingOutInterceptor=ERROR' /opt/karaf/etc/org.ops4j.pax.logging.cfg \
    && sed -i -e '$a log4j.logger.org.apache.cxf.interceptor.LoggingInInterceptor=ERROR' /opt/karaf/etc/org.ops4j.pax.logging.cfg \
    && mkdir -p /opt/karaf/data /opt/karaf/data/log \
    && chown -R $KARAF_USER.$KARAF_USER /opt/karaf \
    && mkdir /opt/karaf/etc/keystore \
    && keytool -genkeypair -keyalg RSA -validity 3650 \
         -alias account-server \
         -dname "cn=identity, ou=hlavki, o=Account Manager, C=SK, L=Bratislava, S=Slovakia" \
         -keypass changeit -storepass changeit \
         -keystore /opt/karaf/etc/keystore/account-manager.jks \
         -ext SAN=DNS:identity.domain,DNS:identity.domain

EXPOSE 8101 8181

USER $KARAF_USER

CMD ["/opt/karaf/bin/karaf", "run"]
