package com.google.sps.data;

public final class Listing {
  private final long id;
  private final String title;
  private final String description;
  private final int capacity;
  private final long timestamp;

  public Listing(long id, String title, String description, int capacity, long timestamp) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.capacity = capacity;
    this.timestamp = timestamp;
  }
}