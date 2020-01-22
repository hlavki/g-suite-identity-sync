package eu.hlavki.identity.services.ldap.config;

public interface Configuration {

    String getBaseDN();


    String getLdapUserBaseDN();


    String getLdapGroupsBaseDN();


    String getLdapGroupsObjectClass();


    String getLdapGroupsMemberAttr();

    
    String getUserDNAttr();
}
