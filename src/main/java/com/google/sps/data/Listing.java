package com.google.sps.data;

import java.util.List;
import java.util.ArrayList;

public final class Listing {
  private final long id;
  private final String title;
  private final String description;
  private final int capacity;
  private final long timestamp;
  private final String author;
  private final List<String> users;

  public Listing(long id, String title, String description, int capacity, long timestamp, String author, List<String> users) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.capacity = capacity;
    this.timestamp = timestamp;
    this.author = author;
    this.users = users;
  }
}