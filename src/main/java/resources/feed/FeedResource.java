package resources.feed;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import models.Article;

import com.google.common.collect.Lists;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import daos.ArticleDao;

@Path("/feed/{type}.xml")
public class FeedResource {

    private ArticleDao articleDao;

    public FeedResource(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @GET
    public StreamingOutput getFeed(@PathParam("type") String type) {
        List<Article> articles = articleDao.findNewestArticles(20);

        String feedType = type;

        final SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);
        feed.setEncoding("UTF-8");

        feed.setTitle("Demo RSS feed");
        feed.setLink("http://localhost:8080/"); // TODO: Make me configurable
        feed.setDescription("Test return rss resource");

        List<SyndEntry> entries = Lists.newArrayList();

        for (Article article : articles) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(article.getTitle());
            entry.setLink("http://localhost:8080/article/" + article.getSlug()); // TODO: Make me configurable
            entry.setPublishedDate(article.getDateCreated());
            SyndContent description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(article.getText());
            entry.setDescription(description);
            entries.add(entry);
        }

        feed.setEntries(entries);

        final SyndFeedOutput syndFeedOutput = new SyndFeedOutput();

        return new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                Writer writer = new OutputStreamWriter(output);
                try {
                    syndFeedOutput.output(feed, writer, true);
                } catch (FeedException e) { // TODO: log me
                    throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
                }
            }
        };
    }
}
