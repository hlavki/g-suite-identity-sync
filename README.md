# GSuite Identity Synchronizer

*Read this in other languages: [English](README.md), [Slovenčina](README_sk.md).*

GSuite Identity Sychronizer synchronize accounts and groups managed by GSuite to LDAP.
Account synchronization is driven by users. It means that account is synchronized when user uses account manager web to create account.
Every account must be GSuite account or regular gmail account with specific GSuite group membership.
There is no possibility to use gsuite password but you can synchronize LDAP password to GSuite.
Groups are synchronized on user creation, scheduler on manually.

## Installation

### GSuite preparation

#### Project creation

1. Open [Developer console](https://console.cloud.google.com/cloud-resource-manager)
1. Click on **Create Project**, type **Project Name** value *Account Manager* and click **Create**
1. Wait for project creation
1. Select created project

#### Allow API

1. Open APIs & Services -> [Library](https://console.cloud.google.com/apis/library)
1. Make sure *Account Manager* project is selected.
1. Search for *Google People API* and then click to **Enable**
1. Repeat previous step with *Google+ API* and *Admin SDK*

#### Create OAuth Consent Screen

1. Open APIs & Services -> Credentials -> [OAuth consent screen](https://console.cloud.google.com/apis/credentials/consent)
1. Setup e-mail
1. Project name: *"Account Manager"*
1. Click **Save**

#### Create authorization credentials for Web Application

1. According to the instructions [1](https://developers.google.com/identity/protocols/OAuth2WebServer#creatingcred) or [2](https://developers.google.com/identity/sign-in/web/server-side-flow#step_1_create_a_client_id_and_client_secret) create Web Application credentials
1. Write e.g. *"Account manager client"* into *Name*
1. Field *Authorized JavaScript origins* leave empty
1. Field *Authorized redirect URIs* fill with
    - `https://localhost:8443/cxf/oidc/rp/complete`
1. Click **Create** and save client ID and client secret.

#### Craete Service account and permissions

1. According to the [instructions](https://developers.google.com/identity/protocols/OAuth2ServiceAccount#creatinganaccount) create Service Account
1. Write e.g. *GSuite services client* into *Name*
1. Check *Furnish a new private key* and choose **p12** for private key format
1. Check *Enable G Suite Domain-wide Delegation*
1. Click **Create** and save p12 file and private key password
1. Open [GSuite Admin Console](https://admin.google.com)
1. Check **Security -> API Reference -> Enable API access**
1. According to the [instructions](https://developers.google.com/identity/protocols/OAuth2ServiceAccount#delegatingauthority) delegate these permissions
    - `https://www.googleapis.com/auth/admin.directory.group.readonly` - Scope for only retrieving group, group alias, and member information.
    - `https://www.googleapis.com/auth/admin.directory.user` - Global scope for access to all user and user alias operations. (Don't need if you don't want to synchronize password to GSuite)

#### What you will need

1. Client ID & secret for *User services client* credentials
1. p12 with private key for GSuite services client encrypted by passphrase

### Installation

#### Start application

Prerequisites to run application is *docker* and *docker compose*.

1. Download [docker-compose.yml](http://github.com/hlavki/) for latest release.
1. Rename p12 file to `service-account.p12` and move it to same directory as docker-compose.yml
1. Edit docker-compose.yml and set LDAP configuration based on your preferences in both services
1. Run `docker-compose up -d` and follow logs with `docker-compose logs -f`

#### Configuration

1. Run `docker cp ./service-account.p12 accountmanager_account_1:/opt/karaf/etc/keystore/`
1. Run command `ssh karaf@localhost -p 8101` or use putty. Password is `karaf`
1. To list all configuration properties run `config:list "(service.pid=eu.hlavki.identity)"`
1. Run karaf commands to set configuration:

```
config:property-set -p eu.hlavki.identity oauth2.serviceAccount.privateKey.passphrase notasecret
config:property-set -p eu.hlavki.identity oauth2.serviceAccount.privateKey.file /opt/karaf/etc/keystore/service-account.p12
```

### Application test

Open in browser [https://localhost:8443/](https://localhost:8443/) and click *Sign in*.

## Build

```
mvn clean install -Pdocker
```

## Generovanie SSL self-signed certifikatu pre proxy komunikaciu Apache HTTPd a Apache karaf

```
/usr/java/default/bin/keytool -genkeypair -keyalg RSA -validity 3650 \
 -alias account-server \
 -dname "cn=account, ou=company s.r.o., o=Account Manager, C=SK, L=Bratislava, S=Slovakia" \
 -keypass changeit -storepass changeit \
 -keystore account-manager.jks \
 -ext SAN=DNS:account.domain,DNS:account.domain

/usr/java/default/bin/keytool -exportcert -alias account-server -storepass changeit -keystore account-manager.jks -rfc -file account-server.pem
```

# Links

https://developers.google.com/identity/protocols/OAuth2WebServer#protectauthcode

offline
https://developers.google.com/identity/sign-in/web/server-side-flow

http://justincalleja.com/2016/04/17/serving-a-webpack-bundle-in-spring-boot/
