package com.io7m.jaccord.tests.core;

import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordNames;
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.core.JaScale;
import com.io7m.jaccord.core.JaScaleHarmonization;
import com.io7m.jaccord.core.JaScaleHarmonizationChordTypes;
import com.io7m.jaccord.core.JaScaleModes;
import com.io7m.jaccord.core.JaScaleNamed;
import com.io7m.jaccord.core.JaScaleNames;
import com.io7m.jaccord.parser.api.JaAccidentalEncoding;
import com.io7m.jaccord.parser.api.JaParseError;
import com.io7m.jaccord.parser.api.JaScaleParserConfiguration;
import com.io7m.jaccord.parser.api.JaScaleParserType;
import com.io7m.jaccord.parser.vanilla.JaScaleParsers;
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

      final JaScaleNames scales = JaScaleNames.open();

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

        final JaScale scale = r.get();

        {
          final List<JaScaleNamed> names =
            scales.lookupByIntervals(scale.intervals());

          names.forEach(
            named -> System.out.printf(
              "Scale name: %s %s\n",
              scale.root().noteName(),
              named.name()));

          System.out.println();
        }

        final Vector<JaChord> chords =
          JaScaleHarmonization.harmonize(
            JaScaleHarmonizationChordTypes.SEVENTH_CHORDS, scale);

        for (int index = 0; index < chords.size(); ++index) {
          final JaChord chord = chords.get(index);
          final String r_name = JaChordNames.name(chord.intervals());

          System.out.printf(
            "%-2s : %s%s (%s)\n",
            chord.root().noteName(),
            chord.root().noteName(),
            r_name,
            chord.notes().map(JaNote::noteName).collect(Collectors.joining(" ")));
        }

        System.out.println();

        final Vector<JaScale> modes = JaScaleModes.modes(scale);

        for (int index = 0; index < modes.size(); ++index) {
          final JaScale mode = modes.get(index);
          System.out.printf(
            "Mode %d: %s\n",
            Integer.valueOf(index),
            mode.notesOrdered().map(JaNote::noteName).collect(Collectors.joining(
              " ")));

          {
            final List<JaScaleNamed> mode_names =
              scales.lookupByIntervals(mode.intervals());

            mode_names.forEach(
              named -> System.out.printf(
                "Mode name: %s %s\n",
                mode.root().noteName(),
                named.name()));

            System.out.println();
          }

          final Vector<JaChord> mode_chords =
            JaScaleHarmonization.harmonize(
              JaScaleHarmonizationChordTypes.SEVENTH_CHORDS, mode);

          for (int mode_chord_index = 0; mode_chord_index < mode_chords.size(); ++mode_chord_index) {
            final JaChord chord = mode_chords.get(mode_chord_index);
            final String r_name = JaChordNames.name(chord.intervals());

            System.out.printf(
              "%-2s : %s%s (%s)\n",
              chord.root().noteName(),
              chord.root().noteName(),
              r_name,
              chord.notes().map(JaNote::noteName).collect(Collectors.joining(" ")));
          }

          System.out.println();
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
