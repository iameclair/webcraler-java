package com.interview.exercise.webcrawler.services;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code LinkRetrieverService} class extracts links from a given HTML content, focusing on links within the same domain.
 */
@Service
public class LinkRetrieverService {

    private static final Logger LOGGER = Logger.getLogger(LinkRetrieverService.class.getName());
    private static final String REGEX = "href=\"(.*?)\"";
    private static final String ALLOWED_SCHEME = "http";

    public Set<String> retrieveLinks(String htmlContent, String domain) {
        Set<String> links = new HashSet<>();
        Pattern pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlContent);

        while (matcher.find()) {
            String link = matcher.group(1);

            link = normalizeLink(link, domain);

            if (link != null && link.startsWith(ALLOWED_SCHEME) && isSameDomain(link, domain)) {
                links.add(link);
            }
        }
        return links;
    }

    protected String normalizeLink(String link, String domain) {
        if (link == null || link.isEmpty() || link.startsWith("#")) {
            LOGGER.log(Level.WARNING, "Invalid Link [%s]. Link cannot be null or empty or start with '#'.".formatted(link));
            return null;
        }

        if (link.contains("#")) {
            link = link.substring(0, link.indexOf("#"));
        }
        try {
            final URI parsedURI = URI.create(link);
            if (parsedURI.isAbsolute()) {
                if (!parsedURI.getSchemeSpecificPart().startsWith("/")) {
                    LOGGER.log(Level.WARNING, "Invalid Link [%s]. This is not a valid web link.".formatted(link));
                    return null;
                }
                return parsedURI.toASCIIString();
            } else {
                return URI.create(domain).resolve(link).toASCIIString();
            }
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Something went wrong while normalizing this link [%s].".formatted(link), exception.getMessage());
            return null;
        }
    }

    private boolean isSameDomain(String link, String domain) {
        return link.startsWith(domain);
    }
}
