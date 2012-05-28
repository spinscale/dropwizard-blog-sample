package web.pages;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;

import com.google.common.collect.Lists;

public class IndexPage extends FluentPage {

    public static final String BASE_URL = "http://localhost:8080";

    @AjaxElement
    FluentWebElement tagcloud;

    @Override
    public String getUrl() {
        return BASE_URL + "/";
    }

    @Override
    public void isAt() {
        assertThat(title(), containsString("my blog"));
    }

    private void waitForTagCloudToBeFilled() {
        await().atMost(5, TimeUnit.SECONDS).until("#tagcloud a").hasSize().greaterThanOrEqualTo(1);
    }

    public List<String> getTagsFromTagCloud() {
        List<String> tags = Lists.newArrayList();
        waitForTagCloudToBeFilled();

        @SuppressWarnings("unchecked")
        FluentList<FluentWebElement> fluentList = tagcloud.find("a");
        for (FluentWebElement element : fluentList) {
            tags.add(element.getText());
        }

        return tags;
    }

    public void clickOnFirstArticleTitle() {
        findFirst("h2 a").click();
        assertNoPageReloadOccured();
    }

    private void assertNoPageReloadOccured() {
        assertThat(url(), is(getUrl())); // No page reload to somewhere else
    }

    public void search(String term) {
        FluentWebElement searchBox = findFirst("#search");
        searchBox.clear();
        searchBox.text(term);
    }
}
