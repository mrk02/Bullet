package com.mrk02.bullet.service.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 *
 */
@Root(strict = false)
public final class Page {

  @Element
  private String title;

  @Element
  private String icon;

  @ElementList
  private List<Board> boards;

  public String title() {
    return title;
  }

  public String icon() {
    return icon;
  }

  public List<Board> boards() {
    return boards;
  }
}
