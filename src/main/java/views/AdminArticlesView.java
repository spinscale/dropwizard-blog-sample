package views;

import java.util.List;

import models.Article;

public class AdminArticlesView extends JsView {

    private List<Article> articles;

    public AdminArticlesView(List<Article> articles) {
        super("/views/js/AdminArticlesView.hbr");
        this.articles = articles;
    }

    public List<Article> getArticles() {
        return articles;
    }
}
