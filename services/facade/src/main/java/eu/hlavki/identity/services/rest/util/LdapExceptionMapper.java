package eu.hlavki.identity.services.rest.util;

import eu.hlavki.identity.services.ldap.LdapSystemException;
import eu.hlavki.identity.services.rest.model.AppError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LdapExceptionMapper implements ExceptionMapper<LdapSystemException> {

    @Override
    public Response toResponse(LdapSystemException exception) {
        return AppError.toResponse("LDAP_ERR", exception).build();
    }
}
