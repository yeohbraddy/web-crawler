package demo.util;

public final class CrawlerUtils {

    public static String extractUrl(String url) {
        return url.split("\\?")[0].split("#")[0];
    }

    public static boolean isSameDomain(String url0, String url1) {
        String domain0 = cleanUrlForDomainComparison(url0);
        String domain1 = cleanUrlForDomainComparison(url1);

        return domain0.equals(domain1);
    }

    private static String cleanUrlForDomainComparison(String url) {
        return extractUrl(url).replaceAll("http[s]?://", "").split("/")[0].toLowerCase();
    }
}