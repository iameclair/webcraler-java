package com.interview.exercise.webcrawler.controller;

import com.interview.exercise.webcrawler.model.Page;
import com.interview.exercise.webcrawler.services.WebcrawlerOrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crawl")
public class WebCrawlerController {
    private final WebcrawlerOrchestratorService webcrawlerOrchestratorService;

    @Autowired
    public WebCrawlerController(final WebcrawlerOrchestratorService webcrawlerOrchestratorService) {
        this.webcrawlerOrchestratorService = webcrawlerOrchestratorService;
    }

    @GetMapping
    public ResponseEntity<?> crawlPage(@RequestParam String url) {
        try {
            List<Page> pages = webcrawlerOrchestratorService.crawl(url).get();
            return ResponseEntity.ok(pages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "Error while crawling page url - %s \n %s.".formatted(url, e.getMessage())
            );
        }

    }
}
