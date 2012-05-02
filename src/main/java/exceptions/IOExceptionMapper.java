package exceptions;

import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class IOExceptionMapper implements ExceptionMapper<IOException> {

    public Response toResponse(IOException exception) {
        return Response.serverError().build();
    }

}
