package com.mrk02.bullet.service.model;

import org.simpleframework.xml.Element;

public final class Board {

  @Element
  private String title;

  @Element
  private Link link;

  public String title() {
    return title;
  }

  public Link link() {
    return link;
  }
}
