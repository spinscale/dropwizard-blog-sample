package views;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

import com.yammer.dropwizard.json.Json;
import com.yammer.metrics.core.TimerContext;

@Provider
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML})
public class JsViewMessageBodyWriter implements MessageBodyWriter<JsView> {

    private Json json;

    public JsViewMessageBodyWriter(Json json) {
        this.json = json;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JsView.class.isAssignableFrom(type);
    }

    public long getSize(JsView t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(JsView jsView, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {

        final TimerContext context = jsView.getRenderingTimer().time();

        try {
            InputStream is = getClass().getResourceAsStream(jsView.getTemplateName());
            String templateString = IOUtils.toString(is);

            String jsonContext = json.writeValueAsString(jsView);
            String renderedTemplate = JsRenderer.renderTemplate(templateString, jsonContext);

            entityStream.write(renderedTemplate.getBytes());
        } finally {
            context.stop();
        }
    }

}
