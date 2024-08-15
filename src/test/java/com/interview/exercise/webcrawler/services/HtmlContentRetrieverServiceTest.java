package com.interview.exercise.webcrawler.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.TestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HtmlContentRetrieverServiceTest {

    @InjectMocks
    private HtmlContentRetrieverService htmlContentRetrieverService;

    @Mock
    private HttpURLConnection mockedConnection;

    @BeforeEach
    void setUp() throws IOException {
        HtmlContentRetrieverService contentRetrieverSpy = Mockito.spy(htmlContentRetrieverService);
        doReturn(mockedConnection).when(contentRetrieverSpy).setupConnection(any());
        this.htmlContentRetrieverService = contentRetrieverSpy;
    }

    @Test
    public void retrieveHtmlContent() throws IOException, URISyntaxException {
        //Given
        final String urlString = "https://www.example.com";
        final String mockedHtmlContent = TestUtils.loadResourceAsString("fake-website/index.html");

        //when
        when(mockedConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockedConnection.getInputStream()).thenReturn(new ByteArrayInputStream(mockedHtmlContent.getBytes()));

        // call
        final String actualHtmlResult = htmlContentRetrieverService.fetchHtmlContent(urlString);

        final String normalizeResult = actualHtmlResult.replaceAll("\\s+", " ").trim();
        final String normalizeMockedContent = mockedHtmlContent.replaceAll("\\s+", " ").trim();

        //assert
        assertThat(normalizeResult.replaceAll("\\s+", "")).isEqualTo(normalizeMockedContent.replaceAll("\\s+", ""));
    }
}