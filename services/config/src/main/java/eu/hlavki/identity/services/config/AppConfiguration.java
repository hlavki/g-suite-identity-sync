package eu.hlavki.identity.services.config;

import java.util.Optional;

public interface AppConfiguration {

    Optional<String> getExternalAccountsGroup();


    void setExternalAccountsGroup(String groupName);
}
