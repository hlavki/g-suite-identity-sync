# GSuite Identity Synchronizer

*Tento dokument si môžeš prečítať aj v týchto jazykoch: [English](README.md).*

## Instalačný postup

### Nastavenie opravnení pre GSuite


#### Vytvorenie projektu

1. Otvor [Developer console](https://console.cloud.google.com/cloud-resource-manager)
1. Klikni na **Create Project**, do **Project Name** daj *xIT Account Manager* a klinkni **Create**
1. Počkaj na vytvorenie projektu
1. Vyber vytvorený projekt

#### Povolenie API

1. Otvor APIs & Services -> [Library](https://console.cloud.google.com/apis/library)
1. Skontroluj, ze ci mas vybraty projekt, ktory si vytvoril
1. Vyhladaj a vyber *Google People API* a potom klikni na **Enable**
1. Zopakuj predchadzajuci krok pre *Google+ API* a *Admin SDK*

#### Vytvorenie OAuth Consent Screen

1. Otvor APIs & Services -> Credentials -> [OAuth consent screen](https://console.cloud.google.com/apis/credentials/consent)
1. Nastav email
1. Nazov projektu: *"Account Manager"*
1. Klikni na **Save**

#### Vytvorenie authorization credentials pre Web Application

1. Podla navodu [1](https://developers.google.com/identity/protocols/OAuth2WebServer#creatingcred) alebo [2](https://developers.google.com/identity/sign-in/web/server-side-flow#step_1_create_a_client_id_and_client_secret) vytvor Web Application credentials
1. Do pola *Name* zadaj napr. *"Account manager client"*
1. Cast *Authorized JavaScript origins* nechaj prazdnu
1. Do pola *Authorized redirect URIs* zadaj 2 hodnoty
    - `https://localhost:8443/cxf/oidc/rp/complete`
1. Klikni na **Create** a ulož si client ID a client secret.

#### Vytvorenie Service account a prav

1. Podľa [navodu](https://developers.google.com/identity/protocols/OAuth2ServiceAccount#creatinganaccount) vytvor Service Account
1. Do poľa *Name* zadaj napr. *GSuite services client*
1. Vyber *Furnish a new private key* and choose **p12** a zvoľ formát **p12**
1. Zaškrtni *Enable G Suite Domain-wide Delegation*
1. Klikni na **Create** a ulož súbor p12 a tiež heslo k privátnemu kľúču
1. Otvor [GSuite Admin Console](https://admin.google.com)
1. Vyber checkbox **Security -> API Reference -> Enable API access**
1. Podla [navodu](https://developers.google.com/identity/protocols/OAuth2ServiceAccount#delegatingauthority) deleguj nasledovne prava
    - `https://www.googleapis.com/auth/admin.directory.group.readonly` - citanie skupin a ich clenov
    - `https://www.googleapis.com/auth/admin.directory.user` - zapisanie user hesla

#### Čo budeš potrebovať

1. Client ID a secret pre *User services client* credentials
1. p12 s klucom pre GSuite services client a heslo k nemu

### Installation

#### Start application

Na spustenie aplikácie je potrebné mať nainštalované *docker* a *docker compose*.

1. Stiahni [docker-compose.yml](http://github.com/hlavki/).
1. Premenuj súbor s privátnym kľučom a priponou p12 na `service-account.p12` a presuň ho do toho istého adresára kde máš docker-compose.yml
1. Zmeň docker-compose.yml nastav konfiguráčné premenné environments
1. Spusti `docker-compose up -d` keď chceš, tak sleduj logy pomocou `docker-compose logs -f`

#### Konfigurácia

1. Spusti `docker cp ./service-account.p12 accountmanager_account_1:/opt/karaf/etc/keystore/`
1. Spusti `ssh karaf@localhost -p 8101` or use putty. Password is `karaf`
1. Pre overenie si môžeš pozrieť ako vyzerá konfigurácia aplikácie `config:list "(service.pid=eu.hlavki.identity)"`
1. V konzole karaf spusti následovné príkazy:

```
config:property-set -p eu.hlavki.identity oauth2.serviceAccount.privateKey.passphrase notasecret
config:property-set -p eu.hlavki.identity oauth2.serviceAccount.privateKey.file /opt/karaf/etc/keystore/service-account.p12
```

## Test aplikácie

Otvor v prehliadači [https://localhost:8443/](https://localhost:8443/) a klikni na *Sign in*.
