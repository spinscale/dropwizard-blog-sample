package views;

import java.util.List;

import models.Article;

import com.yammer.dropwizard.views.View;

public class ArticlesView extends View {

    private List<Article> articles;

    public ArticlesView(List<Article> articles) {
        super("/views/ArticlesView.ftl");
        this.articles = articles;
    }

    public List<Article> getArticles() {
        return articles;
    }

}
