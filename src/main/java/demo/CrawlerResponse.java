package demo;

import lombok.Getter;
import lombok.Builder;

import java.util.List;
import java.util.concurrent.Future;

@Builder
@Getter
public class CrawlerResponse {
    private List<Future<CrawlerResponse>> urlsToCrawl;
    private String rootUrl;
    private int currentDepth;
}