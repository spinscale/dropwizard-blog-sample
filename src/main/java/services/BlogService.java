package services;

import healthchecks.ElasticSearchHealthCheck;
import models.User;
import resources.RootResource;
import resources.admin.AdminResource;
import resources.article.ArticleResource;
import resources.article.SlugResource;
import resources.feed.FeedResource;
import resources.search.SearchResource;
import resources.tags.TagResource;
import resources.tags.TagSearchResource;
import resources.template.TemplateResource;
import views.JsRenderer;
import views.JsViewMessageBodyWriter;

import com.google.common.cache.CacheBuilderSpec;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.auth.CachingAuthenticator;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import com.yammer.dropwizard.bundles.AssetsBundle;
import com.yammer.dropwizard.config.Environment;

import configurations.ApplicationConfiguration;
import daos.ArticleDao;
import daos.UserDao;
import daos.impl.ArticleDaoImpl;
import daos.impl.UserDaoImpl;

/**
 * TODO:
 *
 * - Create more sample content
 * - Admin interface: New post with some WYSIWIG
 * - Find out why persistence is not working when restarting - the YAML articles are always loaded again
 */
public class BlogService extends Service<ApplicationConfiguration> {

    public static void main(String[] argv) throws Exception {
        new BlogService().run(new String[] {"server", "src/main/resources/blogservice.yaml"});
    }

    public BlogService() {
        this("blog");
    }

    protected BlogService(String name) {
        super(name);
        addBundle(new AssetsBundle(AssetsBundle.DEFAULT_PATH, CacheBuilderSpec.disableCaching())); // TODO: Start caching in production mode, but want to clear cache somehow
    }

    @Override
    protected void initialize(ApplicationConfiguration configuration, Environment environment) throws Exception {
        // Create elasticsearch server
        ElasticSearchManager esManager = new ElasticSearchManager(configuration.datadir);
        environment.manage(esManager);

        // Create daos
        ArticleDao articleDao = new ArticleDaoImpl(esManager.getNode(), getJson());
        UserDao userDao = new UserDaoImpl(esManager.getNode(), getJson());

        // Create caching authenticator
        CachingAuthenticator<BasicCredentials, User> authenticator = CachingAuthenticator.wrap(new ElasticSearchAuthenticator(userDao),
                CacheBuilderSpec.parse("maximumSize=5,expireAfterAccess=5m"));
        environment.addProvider(new BasicAuthProvider<User>(authenticator, "Admin stuff"));

        // Create resources
        environment.addResource(new RootResource(articleDao));
        environment.addResource(new SlugResource(articleDao));
        environment.addResource(new SearchResource(articleDao));
        environment.addResource(new ArticleResource(articleDao));
        environment.addResource(new AdminResource(articleDao));
        environment.addResource(new TagResource(articleDao));
        environment.addResource(new TagSearchResource(articleDao));
        environment.addResource(new FeedResource(articleDao)); // streaming resource
        environment.addResource(new TemplateResource());

        // health check
        environment.addHealthCheck(new ElasticSearchHealthCheck(esManager.getNode()));

        // Handlebars JavaScript ServerSide renderer
        environment.addProvider(new JsViewMessageBodyWriter(getJson()));
        JsRenderer.init(configuration.requireJsPath, configuration.otherJs);

        if (configuration.loadInitialData) {
            new InitialDataLoader(configuration, articleDao, userDao, getJson()).load();
        }
    }
}
