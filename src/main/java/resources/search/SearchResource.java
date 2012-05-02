package resources.search;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import models.Article;
import views.ArticlesView;
import daos.ArticleDao;

@Path("/search")
public class SearchResource {

    private ArticleDao articleDao;

    public SearchResource(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @GET
    public ArticlesView search(@QueryParam("term") String term) {
        List<Article> articles = articleDao.findBySlugAndText(term, term);
        return new ArticlesView(articles);
    }
}
