package com.mrk02.bullet.service.model;

import org.simpleframework.xml.Element;

public final class Thread {

  @Element
  private String name;

  @SuppressWarnings("FieldMayBeFinal")
  @Element(required = false)
  private boolean sticky = false;

  @Element
  private String user;

  @Element
  private long timestamp;

  @Element
  private Link link;

  @Element
  private Link latest;

  public String name() {
    return name;
  }

  public String user() {
    return user;
  }

  public long timestamp() {
    return timestamp;
  }

  public boolean sticky() {
    return sticky;
  }

  public Link link() {
    return link;
  }

  public Link latest() {
    return latest;
  }
}
