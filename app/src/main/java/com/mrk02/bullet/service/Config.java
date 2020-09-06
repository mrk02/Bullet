package com.mrk02.bullet.service;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;

/**
 *
 */
public final class Config {

  public static final String MAIN = "main.vm";

  private static final Serializer XML_SERIALIZER = new Persister();

  private final Map<String, Template> templates;

  /**
   * @param templates The templates in this config.
   */
  protected Config(@NonNull Map<String, Template> templates) {
    this.templates = templates;
  }

  /**
   * @param page The name of the page to parse.
   * @param url  The url to parse.
   * @return The parsed page.
   * @throws IOException
   */
  public Page parse(@NonNull String page, @NonNull String url) throws Exception {
    final Template template = Objects.requireNonNull(templates.get(page));

    final Context context = new VelocityContext();

    try (PipedWriter pipedWriter = new PipedWriter();
         PipedReader pipedReader = new PipedReader(pipedWriter)) {

      new Thread(() -> {
        try (Writer writer = pipedWriter) {
          template.merge(context, writer);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }).start();

      return XML_SERIALIZER.read(Page.class, pipedReader);
    }
  }


}
