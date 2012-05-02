package resources.article;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import models.Article;
import models.User;

import com.yammer.dropwizard.auth.Auth;

import daos.ArticleDao;

@Path("/article")
public class ArticleResource {

    private ArticleDao articleDao;

    public ArticleResource(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @PUT
    public void create(@Auth User user, Article article) {
        article.setAuthor(user.getFullname());
        articleDao.store(article);
        throw new WebApplicationException(Status.NO_CONTENT);
    }
}
