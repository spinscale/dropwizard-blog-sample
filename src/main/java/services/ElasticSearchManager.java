package services;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.yammer.dropwizard.lifecycle.Managed;
import com.yammer.dropwizard.logging.Log;

public class ElasticSearchManager implements Managed {

    private final Log log = Log.forClass(this.getClass());
    private Node node;

    public ElasticSearchManager(String datadir) {
        Builder settingsBuilder = ImmutableSettings.settingsBuilder();
        settingsBuilder = settingsBuilder.loadFromClasspath("elasticsearch-local.yml");
        settingsBuilder = settingsBuilder.put("gateway.type", "none");
        settingsBuilder = settingsBuilder.put("cluster.name", "mycluster");
        if (datadir != null) {
            settingsBuilder = settingsBuilder.put("path.data", datadir);
        }


        node = NodeBuilder.nodeBuilder().settings(settingsBuilder).node();

        init();
    }

    public void init() {
        createIndex("articles");
        createIndex("users");

        try {
            String mapping = Resources.toString(getClass().getResource("/elasticsearch-mapping.json"), Charsets.UTF_8);
            node.client().admin().indices().preparePutMapping("articles").setType("article").setSource(mapping).execute().actionGet();
        } catch (IOException e) {
            e.printStackTrace();
        }

        node.client().admin().cluster().health(new ClusterHealthRequest("articles").waitForYellowStatus()).actionGet();
        node.client().admin().cluster().health(new ClusterHealthRequest("users").waitForYellowStatus()).actionGet();
    }

    public void start() throws Exception {
        // TODO: How to fill this? Ask on ML? I need most of my stuff initialized earlier...
    }

    public void stop() throws Exception {
        node.client().admin().indices().prepareFlush("articles").execute().actionGet();
        node.client().admin().indices().prepareFlush("users").execute().actionGet();
        node.stop();
    }

    private void createIndex(String name) {
        log.info("Trying to create index {}", name);
        try {
            CreateIndexResponse response = node.client().admin().indices().prepareCreate(name).setSettings(ImmutableSettings.settingsBuilder().put("numberOfShards", "1")).execute().actionGet();
            log.info("Indexresponse for creating of index {}: {}", name, response.isAcknowledged());
        } catch (IndexAlreadyExistsException e) {}
    }

    public Node getNode() {
        return node;
    }
}
