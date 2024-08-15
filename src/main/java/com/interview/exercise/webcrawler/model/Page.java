package com.interview.exercise.webcrawler.model;

import java.util.Set;

/**
 * The {@code Page} record represent a page
 * @param pageLink - the link that takes you to the page
 * @param subLinks - links that you find on the page
 */
public record Page (String pageLink, Set<String> subLinks){}
