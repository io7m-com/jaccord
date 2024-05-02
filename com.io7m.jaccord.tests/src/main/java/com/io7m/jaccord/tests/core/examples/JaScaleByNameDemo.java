package com.io7m.jaccord.tests.core.examples;

import com.io7m.jaccord.chord_names.vanilla.JaChordNames;
import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordInversions;
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.core.JaScale;
import com.io7m.jaccord.core.JaScaleHarmonization;
import com.io7m.jaccord.core.JaScaleHarmonizationChordTypes;
import com.io7m.jaccord.core.JaScaleIntervals;
import com.io7m.jaccord.core.JaScaleModes;
import com.io7m.jaccord.core.JaScaleNamed;
import com.io7m.jaccord.parser.api.JaAccidentalEncoding;
import com.io7m.jaccord.parser.api.JaChordNoteParserConfiguration;
import com.io7m.jaccord.parser.api.JaParseError;
import com.io7m.jaccord.parser.vanilla.JaNoteParsing;
import com.io7m.jaccord.scales.api.JaScales;
import com.io7m.jlexing.core.LexicalPosition;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import io.vavr.control.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
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
    final List<JaScaleNamed> r_scale = JaScales.scalesByID(args[1]);

    r_scale.forEach(named_scale -> {
      final JaScale scale = JaScale.of(note, named_scale.intervals());
      showNamedScalesOf(scale, named_scale.intervals());
      showSeventhChordsOf(scale);

      final Vector<JaScale> modes = JaScaleModes.modes(scale);
      for (final JaScale mode : modes) {
        showNamedScalesOf(scale, mode.intervals());
      }
    });
  }

  private static void showSeventhChordsOf(
    final JaScale scale)
  {
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

      JaChordInversions.inversions(chord).forEach(inversion -> {
        System.out.printf(
          "    %-2s : %s%s (%s)\n",
          inversion.root().noteName(),
          inversion.root().noteName(),
          r_name,
          inversion.notes().map(JaNote::noteName).collect(Collectors.joining(" ")));
      });
    }
  }

  private static void showNamedScalesOf(
    final JaScale scale,
    final JaScaleIntervals intervals)
  {
    final List<JaScaleNamed> names =
      JaScales.scalesByIntervals(intervals);

    names.forEach(
      named -> System.out.printf(
        "Scale name: %s %s\n",
        scale.root().noteName(),
        named.name()));

    System.out.println();
  }
}
