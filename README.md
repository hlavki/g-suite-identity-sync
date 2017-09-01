# G Suite to LDAP Identity Synchronizer [![Build Status](https://travis-ci.org/hlavki/g-suite-identity-sync.svg?branch=master)](https://travis-ci.org/hlavki/g-suite-identity-sync)

Read [Wiki](https://github.com/hlavki/g-suite-identity-sync/wiki) to [install](https://github.com/hlavki/g-suite-identity-sync/wiki/Install) application.

G Suite Identity Sychronizer synchronize accounts and groups managed by G Suite to LDAP. It can be used as authentication and authorization provider to practicaly any service that supports LDAP (e.g. [Gitlab](https://about.gitlab.com/), [Sonatype Nexus](http://www.sonatype.org/nexus/), [Artifactory](https://www.jfrog.com/artifactory/), etc.) while user & group management is still managed by G Suite.

## Features

* Synchronize G Suite accounts or regular gmail accounts to LDAP
* Synchronize G Suite groups to LDAP groups
* Support for external users (gmail only)
* Synchronize LDAP password to G Suite (One password for everything)
* Synchronize name changes to LDAP
* Support to choose username from G Suite aliases
* Scheduled synchronizers (TODO)


## How it works

Account synchronization is driven by users. It means that account is synchronized when user uses account manager web to create account.
Every account must be G Suite account or regular gmail account with specific G Suite group membership.
There is no possibility to use G Suite password but you can synchronize LDAP password to G Suite.
Groups are synchronized on user creation, scheduler on manually.
