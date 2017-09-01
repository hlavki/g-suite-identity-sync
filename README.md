# GSuite to LDAP Identity Synchronizer [![Build Status](https://travis-ci.org/hlavki/gsuite-identity-sync.svg?branch=master)](https://travis-ci.org/hlavki/gsuite-identity-sync)

Read [Wiki](https://github.com/hlavki/gsuite-identity-sync/wiki) to [install](https://github.com/hlavki/gsuite-identity-sync/wiki/Install) application.

GSuite Identity Sychronizer synchronize accounts and groups managed by GSuite to LDAP. It can be used as authentication and authorization provider to practicaly any service that supports LDAP (e.g. [Gitlab](https://about.gitlab.com/), [Sonatype Nexus](http://www.sonatype.org/nexus/), [Artifactory](https://www.jfrog.com/artifactory/), etc.) while user & group management is still managed by GSuite.

## Features

* Synchronize GSuite accounts or regular gmail accounts to LDAP
* Synchronize GSuite groups to LDAP groups
* Support for external users (gmail only)
* Synchronize LDAP password to GSuite (One password for everything)
* Synchronize name changes to LDAP
* Support to choose username from GSuite aliases
* Scheduled synchronizers (TODO)


## How it works

Account synchronization is driven by users. It means that account is synchronized when user uses account manager web to create account.
Every account must be GSuite account or regular gmail account with specific GSuite group membership.
There is no possibility to use gsuite password but you can synchronize LDAP password to GSuite.
Groups are synchronized on user creation, scheduler on manually.
