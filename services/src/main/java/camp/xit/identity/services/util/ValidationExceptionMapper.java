package camp.xit.identity.services.util;

import camp.xit.identity.services.model.AppError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper extends org.apache.cxf.jaxrs.validation.ValidationExceptionMapper {

    public ValidationExceptionMapper() {
    }


    @Override
    protected Response buildResponse(Response.Status errorStatus, String responseText) {
        return AppError.toResponse("E002", responseText, errorStatus).build();
    }
}
