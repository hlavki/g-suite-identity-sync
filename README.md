# GSuite Identity Synchronizer

*Read this in other languages: [English](README.md), [Slovenčina](README_sk.md).*

GSuite Identity Sychronizer synchronize accounts and groups managed by GSuite to LDAP.
Account synchronization is driven by users. It means that account is synchronized when user uses account manager web to create account.
Every account must be GSuite account or for regular gmail account member of specific GSuite group.
There is no possibility to use gsuite password but you can synchronize LDAP password to GSuite.
Groups are synchronized on user creation, scheduler on manually.

## Installation

### Nastavenie opravnení pre GSuite

#### Project creation

1. Open [Developer console](https://console.cloud.google.com/cloud-resource-manager)
1. Click on **Create Project**, type **Project Name** value *Account Manager* and click **Create**
1. Wait for project creation

#### Allow API

1. Open API Manager -> [Library](https://console.cloud.google.com/apis/library)
1. Make sure *Account Manager* project is selected.
1. Search for *Google People API* and then click to **Enable**
1. Repeat previous step with *Google+ API* and *Admin SDK*

#### Create OAuth Consent Screen

1. Open Api Manager -> [Credentials](https://console.cloud.google.com/apis/credentials/consent)
1. Setup e-mail
1. Project name: *"Account Manager"*
1. Homepage URL `https://accout.domain/`
1. You can choose your company logo

#### Create authorization credentials pre Web Application

1. According to the instructions [1](https://developers.google.com/identity/protocols/OAuth2WebServer#creatingcred) or [2](https://developers.google.com/identity/sign-in/web/server-side-flow#step_1_create_a_client_id_and_client_secret) create Web Application credentials
1. Write e.g. *"User services client"* into *Name*
1. Field *Authorized JavaScript origins* leave empty
1. Field *Authorized redirect URIs* fill with
    - `https://localhost:8443/cxf/oidc/rp/complete`
    - `https://account.domain/cxf/oidc/rp/complete`

#### Craete Service account and permissions

1. According to the [instructions](https://developers.google.com/identity/protocols/OAuth2ServiceAccount#creatinganaccount) create Service Account
1. Write e.g. *GSuite services client* into *Name*
1. Choose **p12** for private key format
1. Check *Enable G Suite Domain-wide Delegation*
1. Open [GSuite Admin Console](https://admin.google.com)
1. Check **Security -> API Reference -> Enable API access**
1. According to the [instructions](https://developers.google.com/identity/protocols/OAuth2ServiceAccount#delegatingauthority) delegate these permissions
    - `https://www.googleapis.com/auth/admin.directory.group.readonly` - Scope for only retrieving group, group alias, and member information.
    - `https://www.googleapis.com/auth/admin.directory.user` - Global scope for access to all user and user alias operations. (Don't need if you don't want to synchronize password to GSuite)

#### What will I need

1. Client ID & secret for *User services client* credentials
1. p12 with private key for GSuite services client encrypted by passphrase

## Build

```
mvn clean install -Pdocker
```

## Web - Local Development

``` bash
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

# build for production with minification
npm run build

# build for production and view the bundle analyzer report
npm run build --report

# run unit tests
npm run unit

# run all tests
npm test
```

For detailed explanation on how things work, checkout the [guide](http://vuejs-templates.github.io/webpack/) and [docs for vue-loader](http://vuejs.github.io/vue-loader).

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
