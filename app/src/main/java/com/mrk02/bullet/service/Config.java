package com.mrk02.bullet.service;

import com.mrk02.bullet.service.model.Page;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.implement.EscapeXmlReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;

/**
 *
 */
public final class Config {

  public static final String MAIN = "main.vm";

  private static final Serializer XML_SERIALIZER = new Persister();
  private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

  private final Map<String, Template> templates;

  /**
   * @param templates The templates in this config.
   */
  protected Config(@NonNull Map<String, Template> templates) {
    this.templates = templates;
  }

  /**
   * @param name The name of the template used parsing the page.
   * @param url  The url to parse.
   * @return The parsed page.
   * @throws Exception if the page could not be parsed.
   */
  public Page parse(@NonNull String name, @NonNull String url) throws Exception {
    try (PipedReader reader = new PipedReader()) {
      final Future<Page> page = EXECUTOR_SERVICE.submit(() -> XML_SERIALIZER.read(Page.class, reader));
      try (Writer writer = new PipedWriter(reader)) {
        parse(name, url, writer);
      }
      return page.get();
    }
  }

  private void parse(@NonNull String name, @NonNull String url, Writer writer) throws IOException {
    final Document document = Jsoup.connect(url).get();

    final EventCartridge eventCartridge = new EventCartridge();
    eventCartridge.addReferenceInsertionEventHandler(new EscapeXmlReference());

    final VelocityContext context = new VelocityContext();
    context.attachEventCartridge(eventCartridge);
    context.put("doc", document);

    final Template template = Objects.requireNonNull(templates.get(name));
    template.merge(context, writer);
  }


}
