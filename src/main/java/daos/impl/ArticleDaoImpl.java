package daos.impl;

import com.google.common.collect.Lists;
import com.yammer.dropwizard.json.Json;
import daos.ArticleDao;
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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.List;

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
        return deleteResponse.isFound();
    }

    public Article findBySlug(String slug) {
        GetResponse response = node.client().prepareGet(INDEX, TYPE, slug).execute().actionGet();
        if (response.isExists()) {
            try {
                return json.readValue(response.getSourceAsString(), Article.class);
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
        boolQuery.should(QueryBuilders.matchQuery("slug", slug));
        boolQuery.should(QueryBuilders.matchQuery("text", text));

        SearchResponse searchResponse = node.client().prepareSearch(INDEX).setTypes(TYPE).
            addSort("dateCreated", SortOrder.DESC).
            setQuery(QueryBuilders.filteredQuery(boolQuery, getNowDateFilter())).execute().actionGet();

        return toArticles(searchResponse);
    }

    public List<Article> findNewestArticles(int count) {
        SearchResponse searchResponse = node.client().prepareSearch(INDEX).
                setTypes(TYPE).
                addSort("dateCreated", SortOrder.DESC).
                setSize(count).
                setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), getNowDateFilter())).
                execute().actionGet();

        return toArticles(searchResponse);
    }

    public List<Article> findByTag(String tag) {
        SearchResponse searchResponse = node.client().prepareSearch(INDEX).setTypes(TYPE).
            addSort("dateCreated", SortOrder.DESC).
            setQuery(QueryBuilders.filteredQuery(QueryBuilders.termQuery("tags", tag), getNowDateFilter())).execute().actionGet();

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
            addAggregation(AggregationBuilders.terms("by_tags").field("tags").size(20)).
            execute().actionGet();

        Terms tagsTerms = searchResponse.getAggregations().get("by_tags");
        
        for (Terms.Bucket entry : tagsTerms.getBuckets()) {
            tags.add(new Tag(entry.getKey(), (int)entry.getDocCount()));
        }

        return tags;
    }

    private RangeFilterBuilder getNowDateFilter() {
        return FilterBuilders.rangeFilter("dateCreated").lte(System.currentTimeMillis());
    }

    private List<Article> toArticles(SearchResponse searchResponse) {
        List<Article> articles = Lists.newArrayList();

        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
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
