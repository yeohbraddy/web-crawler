package demo;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

public class Crawler {
    private static Logger logger = Logger.getLogger(Crawler.class.getCanonicalName());

    public List<String> crawl(String url) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<String> result = new ArrayList<String>();

        try {
            CrawlerWorker mainCrawler = CrawlerWorker.builder()
                    .executorService(executorService)
                    .rootUrl(url)
                    .crawledUrls(new ConcurrentSkipListSet<String>())
                    .currentDepth(1)
                    .maxDepth(100)
                    .build();

            List<String> temp = recursiveCrawl(Collections.singletonList(executorService.submit(mainCrawler)));
            result.addAll(temp);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            executorService.shutdown();
            while (!executorService.isTerminated()) {

            }
        }

        return result;
    }

    private List<String> recursiveCrawl(List<Future<CrawlerResponse>> urlsToCrawl)
            throws InterruptedException, ExecutionException {

        List<String> result = new ArrayList<String>();

        for (Future<CrawlerResponse> urlToCrawl : urlsToCrawl) {
            try {
                CrawlerResponse crawlerResponse = urlToCrawl.get();
                result.add(crawlerResponse.getRootUrl());
                List<Future<CrawlerResponse>> childrenUrlsToCrawl = crawlerResponse.getUrlsToCrawl();

                if (childrenUrlsToCrawl.size() > 0) {
                    result.addAll(recursiveCrawl(childrenUrlsToCrawl));
                }
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }
        }

        return result;
    }
}