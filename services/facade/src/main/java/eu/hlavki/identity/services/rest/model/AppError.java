package eu.hlavki.identity.services.rest.model;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class AppError {

    String code;
    String message;


    public final static ResponseBuilder toResponse(String code, Throwable t) {
        return Response.ok(new AppError(code, t.getMessage())).status(Response.Status.INTERNAL_SERVER_ERROR);
    }


    public final static ResponseBuilder toResponse(String code, String message) {
        return Response.ok(new AppError(code, message)).status(Response.Status.INTERNAL_SERVER_ERROR);
    }


    public final static ResponseBuilder toResponse(String code, String message, Response.Status status) {
        return Response.ok(new AppError(code, message)).status(status);
    }
}
