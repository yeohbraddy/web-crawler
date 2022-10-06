package demo;

import java.util.List;
import java.util.logging.Logger;

public class CrawlerApplication {

  private static Logger logger = Logger.getLogger(CrawlerApplication.class.getCanonicalName());

  public static void main(String[] args) {
    String urlToCrawl = "https://monzo.com/";

    Crawler crawler = new Crawler();
    List<String> result = crawler.crawl(urlToCrawl);

    logger.info("Printing results");
    logger.info(Integer.toString(result.size()));
    for (String url : result) {
      System.out.println(url);
    }
  }
}