package test;

import com.yammer.dropwizard.AbstractService;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.cli.Command;
import com.yammer.dropwizard.config.Configuration;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;
import java.net.URI;


/**
 * JUnit @Rule that'll start and stop a Dropwizard service around each test method.
 *
 * This class might be extended with factory methods for pre-configured http client
 * instance for both the main and the internal service endpoint.
 *
 * @author Kim A. Betti <kim@developer-b.com>
 */
public class DropwizardTestServer<C extends Configuration, S extends Service<C>> implements TestRule {

    private final Class<C> configurationClass;
    private final Class<S> serviceClass;
    private final String config;

    private TestableServerCommand<C> command;
    private S service;

    protected DropwizardTestServer(Class<C> configClass, Class<S> serviceClass, String config) {
        this.configurationClass = configClass;
        this.serviceClass = serviceClass;
        this.config = config;
    }

    public static <C extends Configuration, S extends Service<C>> DropwizardTestServer<C, S> testServer(
                        Class<C> configClass, Class<S> serviceClass, String config) {
        return new DropwizardTestServer<C, S>(configClass, serviceClass, config);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new DropwizardStatement(base);
    }

    public boolean isRunning() {
        return command.isRunning();
    }

    public S getService() {
        return service;
    }

    public URI getPublicRootUri() {
        return command.getRootUriForConnector("main");
    }

    public URI getInternalRootUri() {
        return command.getRootUriForConnector("internal");
    }

    private class DropwizardStatement extends Statement {

        private final Statement base;

        public DropwizardStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            service = serviceClass.newInstance();
            registerTestCommand(service);

            try {
                service.run(new String[] { "test-server", config });
                base.evaluate();
            }
            finally {
                command.stop();
            }
        }

        /**
         * Register a custom command that'll allow us to register our test-server
         * startup logic that in turn will let us shut it down in a controlled fashion.
         *
         * I really don't like using reflection like this, but it's better then introducing
         * a new abstract class in the Service class hierarchy solely for testing purposes.
         */
        private void registerTestCommand(Service<C> service) throws Exception {
            command = new TestableServerCommand<C>(configurationClass);

            Method method = AbstractService.class.getDeclaredMethod("addCommand", Command.class);
            method.setAccessible(true);
            method.invoke(service, command);
        }

    }

}