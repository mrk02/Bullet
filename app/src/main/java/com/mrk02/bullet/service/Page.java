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

  @Element
  private String icon;

  public String getTitle() {
    return title;
  }

  public String getIcon() {
    return icon;
  }
}
