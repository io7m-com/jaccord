/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jaccord.scales.vanilla;

import com.io7m.jaccord.core.JaScaleIntervals;
import com.io7m.jaccord.core.JaScaleNamed;
import com.io7m.jaccord.scales.spi.JaScaleProviderType;
import java.util.Objects;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeMap;
import io.vavr.collection.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * A provider for a set of well-known scales.
 */

public final class JaScalesVanilla implements JaScaleProviderType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JaScalesVanilla.class);

  private final ScalesDatabase database;

  /**
   * Construct a scale provider.
   */

  public JaScalesVanilla()
  {
    this.database = ScalesDatabase.open(loadScaleData());
  }

  private static Properties loadScaleData()
  {
    try {
      final Properties props = new Properties();
      final URL url = JaScalesVanilla.class.getResource("scales.properties");
      try (InputStream stream = url.openStream()) {
        props.load(stream);
      }
      return props;
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }
  }

  @Override
  public SortedSet<String> scales()
  {
    return this.database.by_id.keySet();
  }

  @Override
  public Optional<JaScaleNamed> scaleByID(
    final String id)
  {
    Objects.requireNonNull(id, "ID");
    return this.database.by_id.get(id).toJavaOptional();
  }

  @Override
  public List<JaScaleNamed> scalesByIntervals(
    final JaScaleIntervals intervals)
  {
    Objects.requireNonNull(intervals, "Intervals");
    return this.database.by_intervals.getOrElse(intervals, List.empty());
  }

  private static final class ScalesDatabase
  {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private final Map<JaScaleIntervals, List<JaScaleNamed>> by_intervals;
    private final TreeMap<String, JaScaleNamed> by_id;

    private ScalesDatabase(
      final Map<JaScaleIntervals, List<JaScaleNamed>> in_by_intervals,
      final TreeMap<String, JaScaleNamed> in_by_id)
    {
      this.by_intervals =
        Objects.requireNonNull(in_by_intervals, "By Intervals");
      this.by_id =
        Objects.requireNonNull(in_by_id, "By ID");
    }

    private static JaScaleNamed parseScaleDefinition(
      final String id,
      final String data)
    {
      final String indices_text =
        data.replace("semitones ", "");
      final String[] indices = WHITESPACE.split(indices_text);
      TreeSet<Integer> intervals = TreeSet.empty();
      for (int index = 0; index < indices.length; ++index) {
        final int semitone = Integer.parseInt(indices[index]);
        intervals = intervals.add(Integer.valueOf(semitone));
      }

      final String name = id.replace('_', ' ');
      return JaScaleNamed.of(id, name, JaScaleIntervals.of(intervals));
    }

    private static final class Builder
    {
      private Map<JaScaleIntervals, List<JaScaleNamed>> by_intervals = HashMap.empty();
      private TreeMap<String, JaScaleNamed> by_id = TreeMap.empty();

      Builder()
      {

      }

      void addDefinition(
        final JaScaleNamed def)
      {
        if (LOG.isTraceEnabled()) {
          LOG.trace(
            "registered scale: {} {}",
            def.id(),
            def.intervals().intervals());
        }

        final List<JaScaleNamed> defs;
        if (this.by_intervals.containsKey(def.intervals())) {
          defs = this.by_intervals.get(def.intervals()).get();
        } else {
          defs = List.of(def);
        }

        this.by_intervals = this.by_intervals.put(def.intervals(), defs);
        this.by_id = this.by_id.put(def.id(), def);
      }
    }

    static ScalesDatabase open(
      final Properties props)
    {
      Objects.requireNonNull(props, "Properties");

      final Builder builder = new Builder();

      for (final Object id_raw : props.keySet()) {
        final String id = (String) id_raw;
        final String data = props.getProperty(id).trim();

        if (data.startsWith("semitones")) {
          builder.addDefinition(parseScaleDefinition(id, data));
        }
      }

      for (final Object id_raw : props.keySet()) {
        final String id = (String) id_raw;
        final String data = props.getProperty(id).trim();

        if (data.startsWith("alias")) {
          final String alias_text = data.replace("alias ", "");

          if (builder.by_id.containsKey(alias_text)) {
            final JaScaleNamed def = builder.by_id.get(alias_text).get();
            final String name = id.replace('_', ' ');
            final JaScaleNamed def2 =
              JaScaleNamed.of(alias_text, name, def.intervals());
            builder.addDefinition(def2);
          } else {
            throw new IllegalStateException("Broken alias: " + id);
          }
        }
      }

      return new ScalesDatabase(builder.by_intervals, builder.by_id);
    }
  }
}
