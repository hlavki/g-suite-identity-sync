# xIT Access Account Manager

## Instalačný postup

### Nastavenie opravnení pre GSuite


#### Vytvorenie projektu

1. Otvor [Developer console](https://console.cloud.google.com/cloud-resource-manager)
1. Klikni na **Create Project**, do **Project Name** daj *xIT Account Manager* a klinkni **Create**
1. Počkaj na vytvorenie projektu

#### Povolenie API

1. Otvor API Manager -> [Library](https://console.cloud.google.com/apis/library)
1. Skontroluj, ze ci mas vybraty projekt, ktory si vytvoril
1. Vyhladaj a vyber *Google People API* a potom klikni na **Enable**
1. Zopakuj predchadzajuci krok pre *Google+ API* a *Admin SDK*

#### Vytvorenie OAuth Consent Screen

1. Otvor Api Manager -> [Credentials](https://console.cloud.google.com/apis/credentials/consent)
1. Nastav email
1. Nazov projektu: *"xIT Account Manager"*
1. Homepage URL `https://account.xit.camp/`
1. Popripade pridat logo

#### Vytvorenie authorization credentials pre Web Application

1. Podla navodu [1](https://developers.google.com/identity/protocols/OAuth2WebServer#creatingcred) alebo [2](https://developers.google.com/identity/sign-in/web/server-side-flow#step_1_create_a_client_id_and_client_secret) vytvor Web Application credentials
1. Do pola *Name* zadaj napr. *"User services client"*
1. Cast *Authorized JavaScript origins* nechaj prazdnu
1. Do pola *Authorized redirect URIs* zadaj 2 hodnoty
    - `https://localhost:8443/cxf/oidc/rp/complete`
    - `https://account.xit.camp/cxf/oidc/rp/complete`

#### Vytvorenie Service account a prav

1. Podla [navodu](https://developers.google.com/identity/protocols/OAuth2ServiceAccount#creatinganaccount) vytvor Service Account
1. Do pola *Name* zadaj napr. *GSuite services client*
1. Pri generovani kluca zvol format **p12**
1. Zaskrtni *Enable G Suite Domain-wide Delegation*
1. Otvor [GSuite Admin Console](https://admin.google.com)
1. Zasrtkni checkbox **Security -> API Reference -> Enable API access**
1. Podla [navodu](https://developers.google.com/identity/protocols/OAuth2ServiceAccount#delegatingauthority) deleguj nasledovne prava
    - `https://www.googleapis.com/auth/admin.directory.group.readonly` - citanie skupin a ich clenov
    - `https://www.googleapis.com/auth/admin.directory.user` - zapisanie user hesla

#### Co budem potrebovat

1. Client ID a secret pre *User services client* credentials
1. p12 s klucom pre GSuite services client a heslo k nemu

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
 -dname "cn=account, ou=xIT s.r.o., o=xIT Account Manager, C=SK, L=Bratislava, S=Slovakia" \
 -keypass changeit -storepass changeit \
 -keystore account-manager.jks \
 -ext SAN=DNS:account.xit.camp,DNS:account.xit.camp

/usr/java/default/bin/keytool -exportcert -alias account-server -storepass changeit -keystore account-manager.jks -rfc -file account-server.pem
```

# Links

https://developers.google.com/identity/protocols/OAuth2WebServer#protectauthcode

offline
https://developers.google.com/identity/sign-in/web/server-side-flow

http://justincalleja.com/2016/04/17/serving-a-webpack-bundle-in-spring-boot/
