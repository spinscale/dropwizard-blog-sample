package views;

import models.Article;

public class ArticleView extends JsView {

    private Article article;

    public ArticleView(Article article) {
        super("/views/js/ArticleView.hbr");
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }
}
