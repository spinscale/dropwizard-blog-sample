package healthchecks;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.node.Node;

import com.yammer.metrics.core.HealthCheck;

public class ElasticSearchHealthCheck extends HealthCheck {

    private Node node;

    public ElasticSearchHealthCheck(Node node) {
        super("elasticsearch");
        this.node = node;
    }

    @Override
    protected Result check() throws Exception {
        ClusterHealthResponse clusterHealthResponse = node.client().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
        return Result.healthy("Last status: " + clusterHealthResponse.getStatus().name());
    }

}
