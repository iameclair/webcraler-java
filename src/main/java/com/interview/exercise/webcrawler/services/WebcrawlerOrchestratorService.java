package com.interview.exercise.webcrawler.services;

import com.interview.exercise.webcrawler.model.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code WebcrawlerOrchestratorService} class is used to orchestrates the crawling process
 */
@Service
public class WebcrawlerOrchestratorService {
    private static final Logger LOGGER = Logger.getLogger(WebcrawlerOrchestratorService.class.getName());
    private final WebCrawlerService webCrawlerService;

    public WebcrawlerOrchestratorService(WebCrawlerService webCrawlerService) {
        this.webCrawlerService = webCrawlerService;
    }

    @Async
    public CompletableFuture<List<Page>> crawl(final String startUrl) {
        LOGGER.log(Level.INFO,"Initiate crawl for "+ startUrl);
        return this.webCrawlerService.initiate(startUrl);
    }

}
