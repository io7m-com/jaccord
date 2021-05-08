package com.io7m.jaccord.tests.core.examples;

import com.io7m.jaccord.chord_names.vanilla.JaChordNames;
import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.core.JaScale;
import com.io7m.jaccord.core.JaScaleHarmonization;
import com.io7m.jaccord.core.JaScaleHarmonizationChordTypes;
import com.io7m.jaccord.core.JaScaleModes;
import com.io7m.jaccord.core.JaScaleNamed;
import com.io7m.jaccord.parser.api.JaAccidentalEncoding;
import com.io7m.jaccord.parser.api.JaParseError;
import com.io7m.jaccord.parser.api.JaScaleParserConfiguration;
import com.io7m.jaccord.parser.api.JaScaleParserType;
import com.io7m.jaccord.parser.vanilla.JaScaleParsers;
import com.io7m.jaccord.scales.api.JaScales;
import com.io7m.jlexing.core.LexicalPosition;
import io.vavr.collection.List;
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
import java.util.Arrays;
import java.util.stream.Collectors;

public final class JaScaleExtendedDemo
{
  private static final Logger LOG = LoggerFactory.getLogger(JaScaleExtendedDemo.class);

  private JaScaleExtendedDemo()
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
          r.getError().forEach(JaScaleExtendedDemo::logParseError);
          continue;
        }

        final JaScale scale = r.get();

        {
          final List<JaScaleNamed> names =
            JaScales.scalesByIntervals(scale.intervals());

          names.forEach(
            named -> System.out.printf(
              "Scale name: %s %s\n",
              scale.root().noteName(),
              named.name()));

          System.out.println();
        }

        final var chordTypes =
          Arrays.stream(JaScaleHarmonizationChordTypes.values())
            .filter(c -> {
              switch (c) {
                case SUSPENDED_2_CHORDS:
                case SUSPENDED_4_CHORDS:
                case TRIADS:
                case SIXTH_CHORDS:
                  return false;
                case SEVENTH_CHORDS:
                case NINTH_CHORDS:
                case ELEVENTH_CHORDS:
                case THIRTEENTH_CHORDS:
                  return true;
              }
              return false;
            }).collect(Collectors.toList());

        for (final var chordType : chordTypes) {
          final Vector<JaChord> chords =
            JaScaleHarmonization.harmonize(chordType, scale);

          System.out.println(chordType);

          for (int index = 0; index < chords.size(); ++index) {
            final JaChord chord = chords.get(index);
            final String r_name = JaChordNames.name(chord.intervals());

            System.out.printf(
              "%-2s : %s%s (%s)\n",
              chord.root().noteName(),
              chord.root().noteName(),
              r_name,
              chord.notes().map(JaNote::noteName).collect(Collectors.joining(" "))
            );
          }

          System.out.println();
        }

        System.out.println();
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
