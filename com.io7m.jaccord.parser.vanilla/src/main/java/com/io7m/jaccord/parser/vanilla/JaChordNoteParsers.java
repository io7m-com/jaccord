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

import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordIntervals;
import com.io7m.jaccord.core.JaChordIntervalsType;
import com.io7m.jaccord.core.JaExceptionChord;
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.parser.api.JaChordNoteParserConfiguration;
import com.io7m.jaccord.parser.api.JaChordNoteParserProviderType;
import com.io7m.jaccord.parser.api.JaChordNoteParserType;
import com.io7m.jaccord.parser.api.JaParseError;
import com.io7m.jlexing.core.LexicalPositionMutable;
import io.vavr.collection.Seq;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import io.vavr.collection.Vector;
import io.vavr.control.Validation;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A provider for chord note parsers.
 */

public final class JaChordNoteParsers implements JaChordNoteParserProviderType
{
  /**
   * Construct a provider.
   */

  public JaChordNoteParsers()
  {

  }

  @Override
  public JaChordNoteParserType create(
    final Path path,
    final JaChordNoteParserConfiguration configuration)
  {
    return new Parser(path, configuration);
  }

  private static final class Parser implements JaChordNoteParserType
  {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private final JaChordNoteParserConfiguration config;
    private final LexicalPositionMutable<Path> position;

    private Parser(
      final Path in_path,
      final JaChordNoteParserConfiguration in_configuration)
    {
      Objects.requireNonNull(in_path, "Path");

      this.config =
        Objects.requireNonNull(in_configuration, "Configuration");
      this.position =
        LexicalPositionMutable.create(0, 0, Optional.of(in_path));
    }

    @Override
    public Validation<Seq<JaParseError>, JaChord> parseLine(
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

      return Validation.sequence(results).flatMap(this::buildChord);
    }

    private Validation<Seq<JaParseError>, JaChord> buildChord(
      final Seq<JaNote> notes)
    {
      final JaNote root = notes.head();

      final Seq<JaNote> rest = notes.tail();
      if (rest.isEmpty()) {
        final String separator = System.lineSeparator();
        return Validation.invalid(
          Vector.of(
            JaParseError.of(
              this.position.toImmutable(),
              new StringBuilder(64)
                .append("Too few notes for chord.")
                .append(separator)
                .append("  Expected: At least three notes.")
                .append(separator)
                .append("  Received: ")
                .append(notes.map(Object::toString).collect(Collectors.joining(
                  " ")))
                .append(separator)
                .toString(),
              Optional.empty())));
      }

      SortedSet<Integer> intervals = TreeSet.empty();
      JaNote previous = root;
      int span = 0;
      for (int index = 0; index < rest.length(); ++index) {
        final JaNote current = rest.get(index);
        final int interval = previous.intervalUpTo(current);
        span += interval;
        intervals = intervals.add(Integer.valueOf(span));
        previous = current;
      }

      try {
        JaChordIntervalsType.checkValidChord(intervals);
        return Validation.valid(
          JaChord.of(root, JaChordIntervals.of(intervals)));
      } catch (final JaExceptionChord e) {
        return Validation.invalid(Vector.of(
          JaParseError.of(
            this.position.toImmutable(),
            e.getMessage(),
            Optional.of(e))));
      }
    }
  }
}
