package views;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;

/**
 * Copied from the View class, just to differentiate
 */
public abstract class JsView {
    private final String templateName;
    private final Timer renderingTimer;

    protected JsView(String templateName) {
        this.templateName = resolveName(templateName);
        renderingTimer = Metrics.newTimer(getClass(), "jsrendering");
    }

    private String resolveName(String templateName) {
        if (templateName.startsWith("/")) {
            return templateName;
        }
        final String packagePath = getClass().getPackage().getName().replace('.', '/');
        return String.format("/%s/%s", packagePath, templateName);
    }

    @JsonIgnore
    public String getTemplateName() {
        return templateName;
    }

    @JsonIgnore
    public Timer getRenderingTimer() {
        return renderingTimer;
    }
}
