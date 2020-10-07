package com.mrk02.bullet.service.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(strict = false)
public final class Page {

  @Element
  private String title;

  @Element
  private String icon;

  @ElementList(required = false)
  private List<Breadcrumb> breadcrumbs = new ArrayList<>();

  @ElementList(required = false)
  private List<Board> boards = new ArrayList<>();

  @ElementList(required = false)
  private List<Thread> threads = new ArrayList<>();

  public String title() {
    return title;
  }

  public String icon() {
    return icon;
  }

  public List<Breadcrumb> breadcrumbs() {
    return breadcrumbs;
  }

  public List<Board> boards() {
    return boards;
  }

  public List<Thread> threads() {
    return threads;
  }
}
