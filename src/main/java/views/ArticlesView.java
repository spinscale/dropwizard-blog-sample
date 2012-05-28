package views;

import java.util.List;

import models.Article;

public class ArticlesView extends JsView {

    private List<Article> articles;

    public ArticlesView(List<Article> articles) {
        super("/views/js/ArticlesView.hbr");
        this.articles = articles;
    }

    public List<Article> getArticles() {
        return articles;
    }
}
