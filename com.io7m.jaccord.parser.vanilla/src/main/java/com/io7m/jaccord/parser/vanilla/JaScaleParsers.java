/*
 * Copyright © 2017 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.jaccord.parser.vanilla;

import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.core.JaScale;
import com.io7m.jaccord.core.JaScaleIntervals;
import com.io7m.jaccord.parser.api.JaParseError;
import com.io7m.jaccord.parser.api.JaScaleParserConfiguration;
import com.io7m.jaccord.parser.api.JaScaleParserProviderType;
import com.io7m.jaccord.parser.api.JaScaleParserType;
import com.io7m.jlexing.core.LexicalPositionMutable;
import io.vavr.collection.Seq;
import io.vavr.collection.SortedSet;
import io.vavr.collection.Vector;
import io.vavr.control.Validation;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * A provider for scale parsers.
 */

public final class JaScaleParsers implements JaScaleParserProviderType
{
  /**
   * Construct a provider.
   */

  public JaScaleParsers()
  {

  }

  @Override
  public JaScaleParserType create(
    final Path path,
    final JaScaleParserConfiguration configuration)
  {
    return new Parser(path, configuration);
  }

  private static final class Parser implements JaScaleParserType
  {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private final JaScaleParserConfiguration config;
    private final LexicalPositionMutable<Path> position;

    private Parser(
      final Path in_path,
      final JaScaleParserConfiguration in_configuration)
    {
      Objects.requireNonNull(in_path, "Path");

      this.config =
        Objects.requireNonNull(in_configuration, "Configuration");
      this.position =
        LexicalPositionMutable.create(0, 0, Optional.of(in_path));
    }

    private static Validation<Seq<JaParseError>, JaScale> buildScale(
      final Seq<JaNote> notes)
    {
      final JaNote root = notes.head();
      final Seq<JaNote> rest = notes.tail();
      final SortedSet<Integer> intervals =
        rest.map(note -> Integer.valueOf(root.intervalUpTo(note))).toSortedSet();
      return Validation.valid(JaScale.of(root, JaScaleIntervals.of(intervals)));
    }

    @Override
    public Validation<Seq<JaParseError>, JaScale> parseLine(
      final String line)
    {
      Objects.requireNonNull(line, "Line");

      this.position.setLine(this.position.line() + 1);

      Vector<Validation<Seq<JaParseError>, JaNote>> results = Vector.empty();
      final String[] pieces = WHITESPACE.split(line.trim());
      for (int index = 0; index < pieces.length; ++index) {
        final String segment =
          Objects.requireNonNull(pieces[index], "Segment");

        results = results.append(
          JaNoteParsing.parseNote(
            this.config,
            this.position.toImmutable(),
            segment));
      }

      return Validation.sequence(results).flatMap(Parser::buildScale);
    }
  }
}
