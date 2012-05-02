package resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import views.ArticlesView;

import daos.ArticleDao;

@Path("/")
public class RootResource {

    private ArticleDao articleDao;

    public RootResource(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @GET
    public ArticlesView show() {
        return new ArticlesView(articleDao.findNewestArticles(5));
    }
}
