package daos;

import java.util.List;

import models.Article;
import models.Tag;

public interface ArticleDao {

    void store(Article article);

    boolean deleteBySlug(String slug);

    Article findBySlug(String slug);
    List<Article> findBySlugAndText(String term, String term2);

    List<Article> findNewestArticles(int count);

    List<Article> findAll();

    List<Tag> findTags();

    List<Article> findByTag(String tag);

}
