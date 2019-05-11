package eu.hlavki.identity.services.rest.exception;

import eu.hlavki.identity.services.rest.model.AppError;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger log = LoggerFactory.getLogger(GenericExceptionMapper.class);


    @Override
    public Response toResponse(Exception exception) {
        log.error(null, exception);
        return AppError.toResponse("SERVER_ERR", exception).build();
    }
}
