package views;

import models.Article;

import com.yammer.dropwizard.views.View;

public class ArticleView extends View {

    private Article article;

    public ArticleView(Article article) {
        super("/views/ArticleView.ftl");
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }
}
