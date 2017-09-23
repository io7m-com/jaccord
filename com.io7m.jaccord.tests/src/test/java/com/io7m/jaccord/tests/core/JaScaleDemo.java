package com.io7m.jaccord.tests.core;

import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordNames;
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.core.JaScale;
import com.io7m.jaccord.core.JaScaleHarmonization;
import com.io7m.jaccord.core.JaScaleHarmonizationChordTypes;
import com.io7m.jaccord.parser.api.JaAccidentalEncoding;
import com.io7m.jaccord.parser.api.JaParseError;
import com.io7m.jaccord.parser.api.JaScaleParserConfiguration;
import com.io7m.jaccord.parser.api.JaScaleParserType;
import com.io7m.jaccord.parser.vanilla.JaScaleParsers;
import com.io7m.jlexing.core.LexicalPosition;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import io.vavr.control.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public final class JaScaleDemo
{
  private static final Logger LOG = LoggerFactory.getLogger(JaScaleDemo.class);

  private JaScaleDemo()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    try (final BufferedReader reader =
           new BufferedReader(
             new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

      final JaScaleParserConfiguration config =
        JaScaleParserConfiguration.builder()
          .setAccidentals(JaAccidentalEncoding.UNICODE_AND_ASCII_ACCIDENTALS)
          .build();

      final JaScaleParserType parser =
        new JaScaleParsers().create(Paths.get(""), config);

      while (true) {
        final String line = reader.readLine();
        if (line == null) {
          break;
        }
        final String line_trimmed = line.trim();
        if (line_trimmed.isEmpty()) {
          continue;
        }

        final Validation<Seq<JaParseError>, JaScale> r =
          parser.parseLine(line_trimmed);
        if (r.isInvalid()) {
          r.getError().forEach(JaScaleDemo::logParseError);
          continue;
        }

        final Vector<JaChord> chords =
          JaScaleHarmonization.harmonize(
            JaScaleHarmonizationChordTypes.SIXTH_CHORDS, r.get());

        for (int index = 0; index < chords.size(); ++index) {
          final JaChord chord = chords.get(index);
          final String r_name = JaChordNames.name(chord.intervals());

          System.out.println(
            String.format(
              "%-2s : %s%s (%s)",
              chord.root().noteName(),
              chord.root().noteName(),
              r_name,
              chord.notes().map(JaNote::noteName).collect(Collectors.joining(" "))));
        }
      }
    }
  }

  private static void logParseError(
    final JaParseError error)
  {
    final LexicalPosition<Path> position = error.position();
    if (position.file().isPresent()) {
      final Path file = position.file().get();
      LOG.error(
        "{}:{}: {}",
        file,
        Integer.valueOf(position.line()),
        error.message());
    } else {
      LOG.error(
        "{}: {}",
        Integer.valueOf(position.line()),
        error.message());
    }
    error.exception().ifPresent(ex -> LOG.error("", ex));
  }
}
