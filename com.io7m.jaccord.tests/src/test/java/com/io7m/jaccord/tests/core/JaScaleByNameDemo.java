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
import com.io7m.jaccord.parser.api.JaChordNoteParserConfiguration;
import com.io7m.jaccord.parser.api.JaParseError;
import com.io7m.jaccord.parser.api.JaScaleParserConfiguration;
import com.io7m.jaccord.parser.api.JaScaleParserType;
import com.io7m.jaccord.parser.vanilla.JaNoteParsing;
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
import java.util.Optional;
import java.util.stream.Collectors;

public final class JaScaleByNameDemo
{
  private static final Logger LOG = LoggerFactory.getLogger(JaScaleByNameDemo.class);

  private JaScaleByNameDemo()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    final JaScaleNames scales = JaScaleNames.open();

    final Validation<Seq<JaParseError>, JaNote> r_note =
      JaNoteParsing.parseNote(
      JaChordNoteParserConfiguration.builder().setAccidentals(
        JaAccidentalEncoding.UNICODE_AND_ASCII_ACCIDENTALS).build(),
      LexicalPosition.<Path>builder().setLine(0).setColumn(0).build(),
      args[0]);

    if (r_note.isInvalid()) {
      LOG.error("failed to parse note");
      System.exit(1);
    }

    final JaNote note = r_note.get();
    final Optional<JaScaleNamed> r_scale = scales.lookupByName(args[1]);

    if (r_scale.isPresent()) {
      final JaScaleNamed named_scale = r_scale.get();
      final JaScale scale = JaScale.of(note, named_scale.intervals());

      {
        final List<JaScaleNamed> names =
          scales.lookupByIntervals(named_scale.intervals());

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
    }



  }
}
