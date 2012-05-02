package resources.tags;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import views.ArticlesView;
import daos.ArticleDao;

@Path("/tags/{tag}")
public class TagSearchResource {

    private ArticleDao articleDao;

    public TagSearchResource(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @GET
    public ArticlesView searchByTag(@PathParam("tag") String tag) {
        return new ArticlesView(articleDao.findByTag(tag));
    }
}
