package eu.hlavki.identity.services.rest.config;

import java.util.Optional;
import java.util.Set;

public interface Configuration {

    Optional<String> getAdminGroup();


    boolean isGsuiteSyncPassword();
}
