package com.io7m.jaccord.tests.core.examples;

import com.io7m.jaccord.chord_names.vanilla.JaChordNames;
import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordInversions;
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.core.JaScale;
import com.io7m.jaccord.core.JaScaleHarmonization;
import com.io7m.jaccord.core.JaScaleNamed;
import com.io7m.jaccord.scales.api.JaScales;
import io.vavr.collection.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.io7m.jaccord.core.JaScaleHarmonizationChordTypes.TRIADS;

public final class JaCommonChordDemo
{
  private static final Logger LOG = LoggerFactory.getLogger(JaCommonChordDemo.class);

  private JaCommonChordDemo()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    if (args.length != 4) {
      LOG.error("usage: [note] [scale] [note] [scale]");
      System.exit(1);
    }

    final JaNote source_note = JaNote.valueOf(args[0]);
    final String source_type = args[1];
    final JaNote target_note = JaNote.valueOf(args[2]);
    final String target_type = args[3];

    final JaScaleNamed source_scale = JaScales.scalesByID(source_type).get(0);
    final JaScaleNamed target_scale = JaScales.scalesByID(target_type).get(0);

    final Vector<JaChord> source_chords =
      JaScaleHarmonization.harmonize(TRIADS, JaScale.of(source_note, source_scale.intervals()));
    final Vector<JaChord> target_chords =
      JaScaleHarmonization.harmonize(TRIADS, JaScale.of(target_note, target_scale.intervals()));

    source_chords.forEach(chord -> {
      LOG.debug("source chord: {} {}", chord.root(), JaChordNames.name(chord.intervals()));
    });
    target_chords.forEach(chord -> {
      LOG.debug("target chord: {} {}", chord.root(), JaChordNames.name(chord.intervals()));
    });

    boolean common = false;
    for (final JaChord chord : source_chords) {
      if (target_chords.contains(chord)) {
        LOG.debug("common chord: {} {}", chord.root(), JaChordNames.name(chord.intervals()));
        common = true;
        continue;
      }

      final Vector<JaChord> inversions = JaChordInversions.inversions(chord);
      for (final JaChord inversion : inversions) {
        LOG.debug("inversion: {} {}", inversion.root(), JaChordNames.name(inversion.intervals()));
        if (target_chords.contains(inversion)) {
          LOG.debug("common chord: {} (inversion {})", chord, inversion);
          common = true;
        }
      }
    }

    if (!common) {
      LOG.debug("no common chords");
    }
  }
}
