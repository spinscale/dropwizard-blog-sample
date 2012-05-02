package resources.article;

import static utils.ResourceHelper.notFoundIfNull;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import models.Article;
import models.User;
import views.ArticleView;

import com.yammer.dropwizard.auth.Auth;

import daos.ArticleDao;

@Path("/article/{slug}")
public class SlugResource {

    private ArticleDao dao;

    public SlugResource(ArticleDao dao) {
        this.dao = dao;
    }

    @DELETE
    public void deleteArticle(@Auth User user, @PathParam("slug") String slug) {
        if (!dao.deleteBySlug(slug)) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        throw new WebApplicationException(Status.NO_CONTENT);
    }

    @GET
    public ArticleView getArticle(@PathParam("slug") String slug) {
        Article article = dao.findBySlug(slug);
        notFoundIfNull(article);

        return new ArticleView(article);
    }

}
