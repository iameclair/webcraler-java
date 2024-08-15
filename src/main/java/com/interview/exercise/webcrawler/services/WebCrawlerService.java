package com.interview.exercise.webcrawler.services;

import com.interview.exercise.webcrawler.model.Page;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code WebCrawlerService} class is the core service that handles the crawling logic.
 * Takes a starting url as an input and recursively explores linked pages within the same domain.
 */
@Service
public class WebCrawlerService {
    private static final Logger LOGGER = Logger.getLogger(WebCrawlerService.class.getName());
    private ExecutorService executorService;
    private final HtmlContentRetrieverService htmlContentRetrieverService;
    private final LinkRetrieverService linkRetrieverService;

    public WebCrawlerService(final HtmlContentRetrieverService htmlContentRetrieverService,
                             final LinkRetrieverService linkRetrieverService) {
        this.htmlContentRetrieverService = htmlContentRetrieverService;
        this.linkRetrieverService = linkRetrieverService;
    }

    public CompletableFuture<List<Page>> initiate(final String startUrl) {
        this.executorService = initialiseExecutorService();
        final String domain = discoverDomain(startUrl);
        final Set<String> registeredUrls = new HashSet<>(0);
        final List<Page> pageRecords =  new ArrayList<>(0);
        try {
            crawl(startUrl, domain, registeredUrls, pageRecords).get();
        } catch (Exception allExceptions) {
            LOGGER.log(Level.SEVERE, "Oops something went wrong, while crawling %s".formatted(startUrl), allExceptions.getMessage());
        } finally {
            shutdownExecutor();
        }

        return CompletableFuture.completedFuture(pageRecords);
    }

    protected ExecutorService initialiseExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    private CompletableFuture<Void> crawl(final String urlString, final String domain, Set<String> registeredUrls, List<Page> pageRecords) {
        return CompletableFuture.runAsync(() -> {
            crawlTask(urlString, domain, registeredUrls, pageRecords);
        }, executorService);
    }

    private void crawlTask(final String urlString, final String domain, Set<String> registeredUrls, List<Page> pageRecords) {
        if (registeredUrls.contains(urlString)) {
            return;
        }

        registeredUrls.add(urlString);

        LOGGER.log(Level.INFO, "Crawling " + urlString);
        //fetch content
        final String htmlContent = htmlContentRetrieverService.fetchHtmlContent(urlString);
        if (htmlContent == null || htmlContent.isEmpty()) {
            return;
        }
        //retrieve all link found on this page
        Set<String> links = linkRetrieverService.retrieveLinks(htmlContent, domain);
        Page pageRecord = new Page(urlString, links);
        pageRecords.add(pageRecord);

        List<CompletableFuture<Void>> subTasks = new ArrayList<>();
        for (String link : links) {
            subTasks.add(crawl(link, domain, registeredUrls, pageRecords));
        }

        CompletableFuture.allOf(subTasks.toArray(new CompletableFuture[0]));
    }

    private void shutdownExecutor() {
        executorService.shutdown();
        try {
            boolean completed = executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            if (completed) {
                LOGGER.log(Level.INFO, "All tasks completed");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            LOGGER.log(Level.SEVERE, "The current thread %s was interrupted for the following reason %s".formatted(Thread.currentThread().getName(), e.getMessage()));
        }
    }

    private String discoverDomain(String urlString) {
        try {
            final URL url = URI.create(urlString).toURL();
            return url.getProtocol() + "://" + url.getHost();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error while discovering domain %s".formatted(urlString), e.getMessage());
            throw new IllegalArgumentException("Invalid URL: " + urlString, e);
        }
    }
}
