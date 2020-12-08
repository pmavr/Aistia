package gr.pmavrogiannis.azure.pageobjects;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import gr.pmavrogiannis.azure.components.SqlHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XePage {

    private String selector = "//div[contains(@class,'lazy')]//h2//a";
    private  String pageUrl = "";

    public XePage(SqlHandler agent) {
        pageUrl = agent.executeQueryWithResults("select url from searchfilter where site='xe'").get(0);
        selector = agent.executeQueryWithResults("select selector from searchfilter where site='xe'").get(0);
    }


    public ArrayList<String> getNewPosts(SqlHandler agent) throws IOException {


        ArrayList<String> urlList = new ArrayList<>();

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        HtmlPage page = client.getPage(pageUrl);

        List<HtmlElement> posts = page.getByXPath(selector);

        // for each of them, check whether they exist in the ignored table
        // if one is not in ignored, add it to the urlList
        for (HtmlElement post : posts) {
            StringBuilder builder = new StringBuilder("https://www.xe.gr");
            String url = builder.append(post.getAttribute("href")).toString();
            String query = "select url from ignored where url like '%" + url + "%'";
            ArrayList<String> resultSet = agent.executeQueryWithResults(query);
            if (resultSet.size() == 0) urlList.add(url + '\n' + '\n');
        }
        return urlList;
    }
}
