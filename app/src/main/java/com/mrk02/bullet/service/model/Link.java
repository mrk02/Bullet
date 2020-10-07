package com.mrk02.bullet.service.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

public final class Link {

  @Text
  private String url;

  @Attribute(required = false)
  private String type;

  public String url() {
    return url;
  }

  public String type() {
    return type;
  }
}
