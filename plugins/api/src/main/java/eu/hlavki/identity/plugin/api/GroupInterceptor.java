package eu.hlavki.identity.plugin.api;

import eu.hlavki.identity.plugin.api.model.Group;

public interface GroupInterceptor {

    void groupSynchronized(Group group) throws ProcessException;


    void groupRemoved(Group group) throws ProcessException;
}
