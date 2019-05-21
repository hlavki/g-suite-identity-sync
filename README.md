# [G Suite](https://gsuite.google.com/) to LDAP Identity Synchronizer
[![Join the chat at https://gitter.im/g-suite-identity-sync/Lobby](https://badges.gitter.im/g-suite-identity-sync/Lobby.svg)](https://gitter.im/g-suite-identity-sync/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)  [![Build Status](https://travis-ci.org/hlavki/g-suite-identity-sync.svg?branch=master)](https://travis-ci.org/hlavki/g-suite-identity-sync)[![Docker hub](https://cdn.iconscout.com/icon/free/png-64/docker-4-532129.png)](https://hub.docker.com/r/hlavki/g-suite-identity-sync/)

G Suite Identity Sychronizer synchronize accounts and groups managed by G Suite to LDAP. It can be used as authentication and authorization provider to any service that supports LDAP (e.g. [Gitlab](https://about.gitlab.com/), [Sonatype Nexus](http://www.sonatype.org/nexus/), [Artifactory](https://www.jfrog.com/artifactory/), [Jira](https://www.atlassian.com/software/jira), [Freeradius](https://freeradius.org/), etc.) while user & group management is still managed by G Suite.

Read [Wiki](https://github.com/hlavki/g-suite-identity-sync/wiki) to [install](https://github.com/hlavki/g-suite-identity-sync/wiki/Install) application.

## Features

* Synchronize G Suite accounts or regular gmail accounts to LDAP
* Synchronize G Suite groups to LDAP groups
* Support for external users (gmail only)
* Synchronize LDAP password to G Suite (One password for everything)
* [Push notifications](https://github.com/hlavki/g-suite-identity-sync/wiki/Push-Notifications)
* Synchronize name changes to LDAP
* Support to choose username from G Suite aliases
* Scheduled synchronizers

## What is this good for

This software is good for all G Suite customers who need to use services that support only LDAP authentication or authorization, especially for software startup companies.

## How it works

Account synchronization is driven by users. It means that account is synchronized when user uses account manager web to create account.
Every account must be G Suite account or regular gmail account with specific G Suite group membership.
There is no possibility to use G Suite password but you can synchronize LDAP password to G Suite.
Groups are synchronized on user creation, scheduler on manually.

## Screenshots

![](https://i.imgur.com/NetCiwR.png)

## Developer Notes

### Making release

```bash
mvn clean release:prepare release:perform -Darguments='-Dmaven.javadoc.failOnError=false -Dmaven.deploy.skip=true -Ddocker.skip.push=true'
```
