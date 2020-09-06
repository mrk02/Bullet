package com.mrk02.bullet.service;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import androidx.annotation.NonNull;

/**
 *
 */
public final class ConfigLoader {

  public static final ConfigLoader INSTANCE = new ConfigLoader();

  private ConfigLoader() {
  }

  /**
   * @param inputStream The inputStream from which to load the config.
   * @return The loaded config.
   * @throws IOException    if an IO error occurred.
   * @throws ParseException if a template could not be parsed.
   */
  @NonNull
  public Config load(@NonNull InputStream inputStream) throws IOException, ParseException {
    final Map<String, Template> templates = new HashMap<>();

    try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
      ZipEntry zipEntry;
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        if (!zipEntry.isDirectory()) {
          final String path = zipEntry.getName();
          final String name = path.substring(path.indexOf('/') + 1);
          final Template template = loadTemplate(zipInputStream);
          templates.put(name, template);
        }
        zipInputStream.closeEntry();
      }
    }

    if (!templates.containsKey(Config.MAIN)) {
      throw new IllegalArgumentException("'" + Config.MAIN + "' template is missing");
    }

    return new Config(templates);
  }

  /**
   * @param inputStream The inputStream from which to load the template.
   * @return The loaded template.
   * @throws ParseException if the template could not be parsed.
   */
  private Template loadTemplate(InputStream inputStream) throws ParseException {
    final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    final UnclosableReader unclosableReader = new UnclosableReader(bufferedReader);

    final RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();

    final Template template = new Template();
    final SimpleNode simpleNode = runtimeServices.parse(unclosableReader, template);

    template.setRuntimeServices(runtimeServices);
    template.setData(simpleNode);
    template.initDocument();

    return template;
  }

  /**
   * A reader which can not be closed.
   * For some reason the velocity parser closes the reader given to it, which prevents us from
   * reading the rest of the entries in the zip file. To circumvent this, wrap the reader with this
   * reader which can not be closed.
   */
  private static final class UnclosableReader extends Reader {

    private final Reader reader;

    private UnclosableReader(Reader reader) {
      this.reader = reader;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
      return reader.read(cbuf, off, len);
    }

    @Override
    public void close() {

    }
  }

}
