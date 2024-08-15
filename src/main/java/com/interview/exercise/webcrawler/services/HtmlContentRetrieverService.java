package com.interview.exercise.webcrawler.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  The {@code HtmlContentRetrieverService} class is responsible for Retrieving HTML content from a given URL
 */
@Service
public class HtmlContentRetrieverService {

    private static final Logger LOGGER = Logger.getLogger(HtmlContentRetrieverService.class.getName());
    private static final int TIMEOUT = 5000;
    private static final String CONNECTION_REQUEST_METHOD = "GET";
    private static final String USER_AGENT_KEY = "User-Agent";
    private static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    public String fetchHtmlContent(String urlString) {
        final StringBuilder content = new StringBuilder();
        try {
            final URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = setupConnection(url);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    connection.disconnect();
                }
            } else {
                LOGGER.log(Level.WARNING, "Failed to connect to %s, got a %s response from server.".formatted(urlString, connection.getResponseCode()));
            }

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Malformed URL %s".formatted(urlString));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve content from %s".formatted(urlString), e.getMessage());
        }
        return content.toString();
    }

    protected HttpURLConnection setupConnection(URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(CONNECTION_REQUEST_METHOD);
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        connection.setRequestProperty(USER_AGENT_KEY,USER_AGENT_VALUE);
        return connection;
    }
}
