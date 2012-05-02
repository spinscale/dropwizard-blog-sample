package resources.article;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import models.Article;

import com.sun.jersey.api.client.ClientResponse;
import com.yammer.dropwizard.testing.ResourceTest;

import daos.ArticleDao;

public class SlugResourceTest extends ResourceTest {

    private final Article article = new Article();
    private final ArticleDao articleDao = mock(ArticleDao.class);

    @Override
    protected void setUpResources() throws Exception {
        article.setAuthor("Me");
        article.setSlug("test-slug");
        article.setText(RandomStringUtils.randomAlphabetic(1000));
        article.setTitle("Non random post title");
        when(articleDao.findBySlug("test-slug")).thenReturn(article);
        addResource(new SlugResource(articleDao));
    }

    @Test
    public void simpleResourceTest() throws Exception {
        assertThat("GET requests fetch article by slug",
                   getSlug("test-slug").getText(),
                   is(article.getText()));

        verify(articleDao).findBySlug("test-slug");
    }

    /*
     * Need to do this workaround, because an ArticleView is returned and not a pure article JSON representation
     */
    private Article getSlug(String slug) throws Exception {
        ClientResponse response = client().resource("/article/test-slug").get(ClientResponse.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getEntityInputStream());
        return mapper.readValue(rootNode.get("article"), Article.class);
    }
}
