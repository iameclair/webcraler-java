package com.interview.exercise.webcrawler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.exercise.webcrawler.model.Page;
import com.interview.exercise.webcrawler.services.WebcrawlerOrchestratorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

@SpringBootApplication
@EnableAsync
public class WebcrawlerApplication implements CommandLineRunner {

    private final WebcrawlerOrchestratorService webcrawlerOrchestratorService;
    private final ObjectMapper objectMapper;

    public WebcrawlerApplication(WebcrawlerOrchestratorService webcrawlerOrchestratorService, ObjectMapper objectMapper) {
        this.webcrawlerOrchestratorService = webcrawlerOrchestratorService;
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebcrawlerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            System.out.println("============ Crawling example ================");

            // Example Crawl monzo.com
            String monzoUrl = "https://monzo.com/";
            List<Page> monzoPages = webcrawlerOrchestratorService.crawl(monzoUrl).get();
            printPagesAsJson(monzoPages);

            System.out.println("====================== END ==========================");

        } catch (Exception e) {
            System.err.println("Error while crawling : " + e.getMessage());
        }
    }

    private void printPagesAsJson(List<Page> pages) {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pages);
            System.out.println(json);
        } catch (JsonProcessingException e) {
           System.err.println("Error while parsing JSON : " + e.getMessage());
        }
    }

}
