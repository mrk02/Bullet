package com.mrk02.bullet.service;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 */
@Root(strict = false)
public final class Page {

  @Element
  private String title;

  public String getTitle() {
    return title;
  }

}
