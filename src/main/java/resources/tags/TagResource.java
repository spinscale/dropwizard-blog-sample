package resources.tags;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import models.Tag;

import daos.ArticleDao;

@Path("/tags")
public class TagResource {

    private ArticleDao articleDao;

    public TagResource(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @GET
    public List<Tag> getTags() {
        return articleDao.findTags();
    }
}
