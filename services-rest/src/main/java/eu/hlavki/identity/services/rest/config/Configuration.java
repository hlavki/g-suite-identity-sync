package eu.hlavki.identity.services.rest.config;

import java.util.Set;

public interface Configuration {

    Set<String> getAdmins();


    boolean isGsuiteSyncPassword();


    String getExternalAccountsGroup();
}
