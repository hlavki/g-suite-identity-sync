package eu.hlavki.identity.services.rest.util;

import eu.hlavki.identity.services.rest.model.AppError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper extends org.apache.cxf.jaxrs.validation.ValidationExceptionMapper {

    public ValidationExceptionMapper() {
    }


    @Override
    protected Response buildResponse(Response.Status errorStatus, String responseText) {
        return AppError.toResponse("VALIDATION_ERR", responseText, errorStatus).build();
    }
}
