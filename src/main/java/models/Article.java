package models;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Sets;

public class Article {

    private String slug;
    private String title;
    private String text;
    private String author;
    private Date dateCreated;
    private Set<String> tags = Sets.newHashSet();

    public Article() {}

    public Article(String slug, String title, String text, String author, Date dateCreated) {
        setSlug(slug);
        setText(text);
        setAuthor(author);
        setDateCreated(dateCreated);
        setTitle(title);
    }

    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<String> getTags() {
        return tags;
    }
}
