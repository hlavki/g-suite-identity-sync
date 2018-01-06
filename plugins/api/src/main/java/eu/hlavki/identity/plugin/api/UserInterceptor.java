package eu.hlavki.identity.plugin.api;

import eu.hlavki.identity.plugin.api.model.CreatedUser;

public interface UserInterceptor {

    void userCreated(CreatedUser user) throws ProcessException;
}
