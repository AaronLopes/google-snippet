package com.google.step.snippet.external;

import com.google.appengine.api.datastore.Entity;
import com.google.step.snippet.data.Card;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public final class W3SchoolsClient implements Client {
  private static final String TITLE_TAG = "h1";
  private static final String DESC_TAG = "p";
  private static final String SNIPPET_CLASS = "w3-example";
  private static final String CODE_CLASS = "w3-code";
  private static final String UP = "upvote";
  private static final String DOWN = "downvote";

  private String cseId = null;

  public W3SchoolsClient(String cseId) {
    this.cseId = cseId;
  }

  @Override
  public String getCseId() {
    return cseId;
  }

  /**
   * Creates and returns a {@code Card} for the given W3Schools URL.
   *
   * @param w3Link the URL of the W3Schools web page to create the card for
   * @return the created card, or {@code null} if a card could not be created
   */
  @Override
  public Card search(String w3Link) {
    Document doc = null;
    try {
      doc = Jsoup.connect(w3Link).get();
    } catch (IOException e) {
      return null;
    }
    Elements titles = doc.getElementsByTag(TITLE_TAG);
    if (titles.isEmpty() || titles.first().text().isEmpty()) {
      return null;
    }
    Elements descriptions = doc.getElementsByTag(DESC_TAG);
    if (descriptions.isEmpty() || descriptions.first().text().isEmpty()) {
      return null;
    }
    Elements snippets = doc.getElementsByClass(SNIPPET_CLASS);
    if (snippets.isEmpty() || snippets.first().getElementsByClass(CODE_CLASS).text().isEmpty()) {
      return null;
    }
    String title = titles.first().text();
    String description = descriptions.first().text();
    String code = snippets.first().getElementsByClass(CODE_CLASS).text();

    /* Retrieve feedback, if stored feedback exists */
    long upvote = 0;
    long downvote = 0;
    Entity feedback = getFeedback(w3Link);
    if (feedback != null) {
      upvote = (long) feedback.getProperty(UP);
      downvote = (long) feedback.getProperty(DOWN);
    }
    return new Card(title, code, w3Link, description, upvote, downvote);
  }
}
