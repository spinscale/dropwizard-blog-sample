package utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

public class ResourceHelper {

    public static void notFoundIfNull(Object obj) {
        errorIfNull(obj, Status.NOT_FOUND);
    }

    public static void errorIfNull(Object obj, Status status) {
        if (obj == null) {
            throw new WebApplicationException(status);
        }
    }

}
