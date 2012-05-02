package models;

public class Tag {

    public Tag() {}

    public Tag(String word, int count) {
        this.word = word;
        this.count = count;
        href = "/tags/" + word;
    }

    public String word;
    public Integer count;
    public String href;
}
