package camp.xit.identity.services.model;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServerError {

    String code;
    String message;


    public ServerError() {
    }


    public ServerError(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public final static ResponseBuilder toResponse(String code, Throwable t) {
        return Response.ok(new ServerError(code, t.getMessage())).status(Response.Status.INTERNAL_SERVER_ERROR);
    }
}
