package resources.admin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import views.AdminArticlesView;

import models.User;

import com.yammer.dropwizard.auth.Auth;

import daos.ArticleDao;

@Path("/admin")
public class AdminResource {

    private ArticleDao articleDao;

    public AdminResource(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @GET
    public AdminArticlesView index(@Auth User user) {
        return new AdminArticlesView(articleDao.findAll());
    }

}
