package com.interview.exercise.webcrawler.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.TestUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LinkRetrieverServiceTest {

    @InjectMocks
    private LinkRetrieverService linkRetrieverService;

    @Test
    void retrieveLinks() throws IOException, URISyntaxException {
        //Given
        final String htmlContent = TestUtils.loadResourceAsString("fake-website/index.html");
        final String domain = "https://example.com";

        //call
        Set<String> actualLinks = linkRetrieverService.retrieveLinks(htmlContent, domain);

        //assert
        assertThat(actualLinks).hasSize(3);
        assertThat(actualLinks).containsExactlyInAnyOrder(
                "https://example.com/contact.html",
                "https://example.com/services.html",
                "https://example.com/about.html"
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com",
            "about.html",
            "/about.html",
            "//about.html",
            "",
            "/",
            "mailto:fakewebsite@gmail.com"
    })
    void normalizeLink(String url) {
        final String domain = "https://example.com";

        final String result = linkRetrieverService.normalizeLink(url, domain);

        if (url.equals("mailto:fakewebsite@gmail.com")) {
            assertThat(result).isNull();
        }

        if (url.equals("about.html")) {
            assertThat(result).isEqualTo("https://example.com/about.html");
        }

        if (url.equals("/about.html")) {
            assertThat(result).isEqualTo("https://example.com/about.html");
        }
        if (url.equals("//about.html")) {
            assertThat(result).isEqualTo("https://about.html");
        }

        if (url.isEmpty()) {
            assertThat(result).isNull();
        }

        if (url.equals("/")) {
            assertThat(result).isEqualTo("https://example.com/");
        }
    }
}