package test;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.cli.CommandLine;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;

import com.yammer.dropwizard.AbstractService;
import com.yammer.dropwizard.cli.ConfiguredCommand;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.config.HttpConfiguration;
import com.yammer.dropwizard.config.ServerFactory;
import com.yammer.dropwizard.logging.Log;

/**
 * Normally ServerCommand is in charge of starting the service, but that's not particularly
 * well suited for integration testing as it joins the current thread and keeps the Server
 * instance to itself.
 *
 * This implementation is based on the original ServerCommand, but in addition to being
 * stoppable it provides a few convenience methods for tests.
 *
 * @author Kim A. Betti <kim@developer-b.com>
 */
public class TestableServerCommand<T extends Configuration> extends ConfiguredCommand<T> {

    private final Log log = Log.forClass(TestableServerCommand.class);

    private final Class<T> configurationClass;

    private Server server;

    public TestableServerCommand(Class<T> configurationClass) {
        super("test-server", "Starts an HTTP test-server running the service");
        this.configurationClass = configurationClass;
    }

    @Override
    protected Class<T> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    protected void run(AbstractService<T> service, T configuration, CommandLine params) throws Exception {
        server = initializeServer(service, configuration);

        try {
            server.start();
        }
        catch (Exception e) {
            log.error(e, "Unable to start test-server, shutting down");
            server.stop();
        }
    }

    public void stop() throws Exception {
        try {
            stopJetty();
        }
        finally {
            unRegisterLoggingMBean();
        }
    }

    /**
     * We won't be able to run more then a single test in the same JVM instance unless
     * we do some tidying and un-register a logging m-bean added by Dropwizard.
     */
    private void unRegisterLoggingMBean() throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName loggerObjectName = new ObjectName("com.yammer:type=Logging");
        if (server.isRegistered(loggerObjectName)) {
            server.unregisterMBean(loggerObjectName);
        }
    }

    private void stopJetty() throws Exception {
        if (server != null) {
            server.stop();
            checkArgument(server.isStopped());
        }
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public URI getRootUriForConnector(String connectorName) {
        try {
            Connector connector = getConnectorNamed(connectorName);
            String host = connector.getHost() != null ? connector.getHost() : "localhost";
            return new URI("http://" + host + ":" + connector.getPort());
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private Connector getConnectorNamed(String name) {
        Connector[] connectors = server.getConnectors();
        for (Connector connector : connectors) {
            if (connector.getName().equals(name)) {
                return connector;
            }
        }

        throw new IllegalStateException("No connector named " + name);
    }

    private Server initializeServer(AbstractService<T> service, T configuration) throws Exception {
        Environment environment = getInitializedEnvironment(service, configuration);
        ServerFactory serverFactory = getServerFactory(service, configuration);

        return serverFactory.buildServer(environment);
    }

    private ServerFactory getServerFactory(AbstractService<T> service, T configuration) {
        HttpConfiguration httpConfig = configuration.getHttpConfiguration();
        return new ServerFactory(httpConfig, service.getName());
    }

    private Environment getInitializedEnvironment(AbstractService<T> service, T configuration) throws Exception {
        Environment environment = new Environment(configuration, service);
        service.initializeWithBundles(configuration, environment);
        return environment;
    }

}