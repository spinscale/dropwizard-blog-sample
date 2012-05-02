package views;

import java.util.List;

import models.Article;

import com.yammer.dropwizard.views.View;

public class AdminArticlesView extends View {

    private List<Article> articles;

    public AdminArticlesView(List<Article> articles) {
        super("/views/AdminArticlesView.ftl");
        this.articles = articles;
    }

    public List<Article> getArticles() {
        return articles;
    }

}
