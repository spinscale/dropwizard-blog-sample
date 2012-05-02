package services;

import java.io.File;
import java.io.IOException;

import models.Article;
import models.User;

import com.yammer.dropwizard.json.Json;
import com.yammer.dropwizard.logging.Log;

import configurations.ApplicationConfiguration;
import daos.ArticleDao;
import daos.UserDao;

public class InitialDataLoader {

    private final Log log = Log.forClass(this.getClass());

    private UserDao userDao;
    private ApplicationConfiguration configuration;
    private ArticleDao articleDao;
    private Json json;

    public InitialDataLoader(ApplicationConfiguration configuration,
            ArticleDao articleDao, UserDao userDao, Json json) {
        this.configuration = configuration;
        this.articleDao = articleDao;
        this.userDao = userDao;
        this.json = json;
    }

    public void load() {
        createAdminUserIfNeeded();
        createEntriesIfNeeded();
    }

    private void createEntriesIfNeeded() {
        File articleDir = new File(configuration.initialDataDirectory);

        if (articleDir.exists() && articleDir.isDirectory()) {
            for (File articleFile : articleDir.listFiles()) {
                if (!articleFile.getName().endsWith(".yaml")) {
                    log.info("Skipping article file {}", articleFile.getName());
                    continue;
                }

                log.info("Checking for article file '{}'", articleFile.getName());
                try {
                    String slug = articleFile.getName().replaceAll(".yaml", "");
                    Article article = articleDao.findBySlug(slug);
                    if (article == null) {
                        log.info("Adding new article [{}]", slug);
                        article = json.readYamlValue(articleFile, Article.class);
                        article.setSlug(slug);
                        articleDao.store(article);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createAdminUserIfNeeded() {
        User admin = userDao.findByUsername(configuration.initialDataAdminUsername);
        if (admin == null) {
            User user = new User("admin", configuration.initialDataAdminPassword);
            user.setFullname(configuration.initialDataAdminPassword);
            userDao.store(user);
        }
    }
}
