package com.mrk02.bullet.service.model;

import org.simpleframework.xml.Element;

public final class Thread {

  @Element
  private String name;

  @Element
  private Link link;

  public String name() {
    return name;
  }

  public Link link() {
    return link;
  }
}
