package configurations;

import com.yammer.dropwizard.config.Configuration;

public class ApplicationConfiguration extends Configuration {

    public String host = "localhost";
    public int port = 9300;

    public String datadir;

    public boolean loadInitialData = false;
    public String initialDataAdminUsername;
    public String initialDataAdminPassword;
    public String initialDataAdminFullname;
    public String initialDataDirectory;
}
