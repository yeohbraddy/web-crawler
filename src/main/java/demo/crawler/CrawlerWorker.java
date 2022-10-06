package demo.crawler;

import lombok.Builder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import demo.response.CrawlerResponse;
import demo.util.CrawlerUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import java.util.List;
import java.util.ArrayList;

@Builder
public class CrawlerWorker implements Callable<CrawlerResponse> {

    private ExecutorService executorService;
    private String rootUrl;
    private int currentDepth;
    private int maxDepth;
    /*
     * We use this data structure because it is a container that allows multiple
     * threads to access it
     * It is a skip list because there is no Red-Black tree implementation of it
     */
    private ConcurrentSkipListSet<String> crawledUrls;

    public CrawlerResponse call() throws Exception {

        List<Future<CrawlerResponse>> urlsToCrawl = new ArrayList<Future<CrawlerResponse>>();

        String nextUrl = CrawlerUtils.extractUrl(rootUrl);

        if (isValidUrlToCrawl(nextUrl)) {
            crawledUrls.add(rootUrl);

            Document doc = Jsoup.connect(nextUrl).ignoreContentType(true).get();
            Elements urlsOnPage = doc.select("a[href]");

            int nextDepth = currentDepth + 1;

            if (nextDepth < maxDepth) {
                for (Element e : urlsOnPage) {
                    String temp = e.attr("abs:href");
                    String url = CrawlerUtils.extractUrl(temp);

                    if (this.isValidUrlToCrawl(url)) {
                        CrawlerWorker worker = CrawlerWorker.builder()
                                .rootUrl(url)
                                .executorService(this.executorService)
                                .crawledUrls(crawledUrls)
                                .currentDepth(nextDepth)
                                .build();
                        urlsToCrawl.add(executorService.submit(worker));
                    }
                }
            }
        }

        return CrawlerResponse.builder()
                .rootUrl(rootUrl)
                .urlsToCrawl(urlsToCrawl)
                .currentDepth(currentDepth)
                .build();
    }

    private boolean isValidUrlToCrawl(String url) {
        return !crawledUrls.contains(url) && !url.trim().isEmpty() && CrawlerUtils.isSameDomain(rootUrl, url);
    }
}