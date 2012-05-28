package resources.template;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;


@Path("/template/{template}")
public class TemplateResource {

    public static final String PATH = "src/main/resources/views/js";

    @GET
    public String get(@PathParam("template") String template) {
        String templatePath = PATH + "/" + template + ".hbr";
        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        try {
            return Files.toString(templateFile, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new WebApplicationException();
        }
    }
}
