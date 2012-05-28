package rhino;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import views.JsRenderer;

public class JsTest extends Assert {

    @Test
    public void testJs() throws IOException {
        List<String> javaScripts = Lists.newArrayList("src/main/resources/assets/js/render/handlebars-1.0.0.beta.6.js",
                "src/main/resources/assets/js/render/handlebars-helper.js");
        JsRenderer.init("src/main/resources/assets/js", javaScripts);

        String templateSource = "<div class=\"row\">" +
                                "<h2><a href=\"/article/{{article.slug}}\">{{article.title}}</a></h2>" +
                                "<h3>by {{article.author}}, at {{formatDate article.dateCreated}}</h3>" +
                                "<div>{{{article.text}}}</div>" +
                                "</div>";

        String articleJson = "{ \"article\": { \"title\":\"TITLE\", \"slug\":\"SLUG\", \"author\":\"AUTHOR\", \"text\":\"TEXT\", \"dateCreated\":1335125532000 } }";
        String renderedTemplate = JsRenderer.renderTemplate(templateSource, articleJson);

        // Checking for formatted date via handlebars
        assertTrue(renderedTemplate.contains("Apr 22, 2012"));
    }
}
