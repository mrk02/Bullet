package com.mrk02.bullet.service;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

/**
 *
 */
public final class Config {

  public static final String MAIN = "main.vm";

  private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);
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

      EXECUTOR_SERVICE.submit(() -> {
        template.merge(context, pipedWriter);
        pipedWriter.close();
        return null;
      });
      return EXECUTOR_SERVICE.submit(() -> XML_SERIALIZER.read(Page.class, pipedReader)).get();
    }
  }


}
