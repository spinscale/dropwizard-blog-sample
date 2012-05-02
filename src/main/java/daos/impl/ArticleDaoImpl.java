package daos.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Article;
import models.Tag;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.google.common.collect.Lists;
import com.yammer.dropwizard.json.Json;

import daos.ArticleDao;

public class ArticleDaoImpl implements ArticleDao {

    private static final String INDEX = "articles";
    private static final String TYPE = "article";

    private Node node;
    private Json json;

    public ArticleDaoImpl(Node node, Json json) {
        this.node = node;
        this.json = json;
    }

    public boolean deleteBySlug(String slug) {
        DeleteResponse deleteResponse = node.client().prepareDelete(INDEX, TYPE, slug).execute().actionGet();
        return !deleteResponse.notFound();
    }

    public Article findBySlug(String slug) {
        GetResponse response = node.client().prepareGet(INDEX, TYPE, slug).execute().actionGet();
        if (response.exists()) {
            try {
                return json.readValue(response.sourceAsString(), Article.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void store(Article article) {
        byte[] data = json.writeValueAsBytes(article);
        node.client().prepareIndex(INDEX, TYPE, article.getSlug()).setSource(data).execute().actionGet();
    }

    public List<Article> findBySlugAndText(String slug, String text) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.textQuery("slug", slug));
        boolQuery.should(QueryBuilders.textQuery("text", text));

        SearchResponse searchResponse = node.client().prepareSearch(INDEX).setTypes(TYPE).
            addSort("dateCreated", SortOrder.DESC).
            setFilter(getNowDateFilter()).
            setQuery(boolQuery).execute().actionGet();

        return toArticles(searchResponse);
    }

    public List<Article> findNewestArticles(int count) {
        SearchResponse searchResponse = node.client().prepareSearch(INDEX).
                setTypes(TYPE).
                setQuery(QueryBuilders.matchAllQuery()).
                setFilter(getNowDateFilter()).
                addSort("dateCreated", SortOrder.DESC).
                setSize(count).
                execute().actionGet();

        return toArticles(searchResponse);
    }

    public List<Article> findByTag(String tag) {
        SearchResponse searchResponse = node.client().prepareSearch(INDEX).setTypes(TYPE).
            addSort("dateCreated", SortOrder.DESC).
            setFilter(getNowDateFilter()).
            setQuery(QueryBuilders.termQuery("tags", tag)).execute().actionGet();

        return toArticles(searchResponse);
    }

    public List<Article> findAll() {
        SearchResponse searchResponse = node.client().prepareSearch(INDEX).setTypes(TYPE).
            setQuery(QueryBuilders.matchAllQuery()).
            addSort("dateCreated", SortOrder.DESC).
            setSize(1000).
            execute().actionGet();

        return toArticles(searchResponse);
    }

    public List<Tag> findTags() {
        List<Tag> tags = Lists.newArrayList();

        TermsFacetBuilder facetBuilder = new TermsFacetBuilder("tags");
        facetBuilder.field("tags").size(20);
        facetBuilder.facetFilter(getNowDateFilter());

        SearchResponse searchResponse = node.client().prepareSearch(INDEX).
            setTypes(TYPE).
            setQuery(QueryBuilders.matchAllQuery()).
            setSize(0).
            addFacet(facetBuilder).
            execute().actionGet();

        TermsFacet facetResult = searchResponse.facets().facet(TermsFacet.class, "tags");
        for (TermsFacet.Entry entry : facetResult.entries()) {
            tags.add(new Tag(entry.term(), entry.count()));
        }

        return tags;
    }

    private RangeFilterBuilder getNowDateFilter() {
        return FilterBuilders.rangeFilter("dateCreated").lte(System.currentTimeMillis());
    }

    private List<Article> toArticles(SearchResponse searchResponse) {
        List<Article> articles = Lists.newArrayList();

        for (SearchHit searchHit : searchResponse.hits().hits()) {
            try {
                Article article = json.readValue(searchHit.source(), Article.class);
                articles.add(article);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return articles;
    }
}
