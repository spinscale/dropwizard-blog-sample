package web;

import org.fluentlenium.adapter.FluentTest;
import org.junit.BeforeClass;

import services.BlogService;

public class BaseTest extends FluentTest {

    public static final String BASE_URL = "http://localhost:8080";

    public static Thread serviceThread;
    @BeforeClass
    public static void beforeClass() throws Exception {
        Runnable run = new Runnable() {
            public void run() {
                try {
                    BlogService.main(new String[]{});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        serviceThread = new Thread(run);
        serviceThread.start();
        // TODO: This sucks
        // Possible solution: Use the maven integration-test lifecycle, and start the service upfront
        // Open for other ideas
        Thread.sleep(10000);
    }

    // Uncomment if you want to use HTML driver, does not work with the ajax stuff though, so pretty pointless
//    public WebDriver webDriver = new HtmlUnitDriver();
//
//    @Override
//    public WebDriver getDefaultDriver() {
//        return webDriver;
//    }
}
