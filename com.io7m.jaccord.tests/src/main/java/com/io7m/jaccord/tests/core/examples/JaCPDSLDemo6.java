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

package com.io7m.jaccord.tests.core.examples;

import com.io7m.jaccord.cpdsl.JaCPDSL;
import com.io7m.jaccord.cpdsl.midi.JaCPDSLExporter;
import com.io7m.jaccord.cpdsl.midi.JaCPDSLExporterConfiguration;
import io.vavr.collection.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.io7m.jaccord.core.JaNote.*;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.*;

public final class JaCPDSLDemo6
{
  private JaCPDSLDemo6()
  {

  }

  public static void main(
    final String[] args)
    throws IOException, InvalidMidiDataException
  {
    final JaCPDSL d = JaCPDSL.create();

    final JaCPDSL.Scale hearts = d.scale(D_SHARP, "Major");
    final JaCPDSL.Scale clubs = d.scale(C, "Natural_Minor");
    final JaCPDSL.Scale diamonds = d.scale(A_SHARP, "Major");
    final JaCPDSL.Scale spades = d.scale(G, "Natural_Minor");

    Vector<JaCPDSL.Change> changes = Vector.empty();

    changes = suit(d, changes, hearts, clubs);

//    changes = changes.append(d.change(d.secondaryDominant(d.diatonic(clubs, V)), 7));
//    changes = changes.append(d.change(d.diatonic7(clubs, V), 7));
//    changes = changes.append(d.change(d.diatonic(clubs, I), 7));
//    changes = suit(d, changes, clubs, diamonds);
//
//    changes = changes.append(d.change(d.secondaryDominant(d.diatonic(diamonds, V)), 7));
//    changes = changes.append(d.change(d.diatonic7(diamonds, V), 7));
//    changes = changes.append(d.change(d.diatonic(diamonds, I), 7));
//    changes = suit(d, changes, diamonds, spades);
//
//    changes = changes.append(d.change(d.secondaryDominant(d.diatonic(spades, V)), 7));
//    changes = changes.append(d.change(d.diatonic7(spades, V), 7));
//    changes = changes.append(d.change(d.diatonic(spades, I), 7));
//    changes = suit(d, changes, spades, spades);

    System.out.println("changes: " + changes.size());
    final JaCPDSL.Progression p = d.progressionOfAll(changes);
    System.out.println(p);

    final JaCPDSLExporterConfiguration configuration =
      JaCPDSLExporterConfiguration.builder()
        .setDoubleRoot(true)
        .setOmitFifth(true)
        .build();

    try (final OutputStream out = Files.newOutputStream(Paths.get("output_cards.mid"))) {
      final Sequence sequence = JaCPDSLExporter.exportWithConfiguration(configuration, p);
      MidiSystem.write(sequence, 1, out);
    }
  }

  private static Vector<JaCPDSL.Change> suit(
    final JaCPDSL d,
    Vector<JaCPDSL.Change> changes,
    final JaCPDSL.Scale current_suit,
    final JaCPDSL.Scale next_suit)
  {
    changes = changes.append(d.change(d.diatonic(current_suit, I), 2));

    changes = changes.append(d.change(
      d.chromaticPassing(d.diatonic(current_suit, I),
                         d.diatonic(current_suit, III),
                         d.noteOfDegree(current_suit, III).stepBy(-1)),
      2));

    changes = changes.append(d.change(d.diatonic(current_suit, III), 2));

    changes = changes.append(d.change(
      d.chromaticPassing(d.diatonic(current_suit, III),
                         d.diatonic(current_suit, II),
                         d.noteOfDegree(current_suit, II).stepBy(1)),
      2));

    changes = changes.append(d.change(d.diatonic(current_suit, II), 2));

    changes = changes.append(d.change(
      d.chromaticPassing(d.diatonic(current_suit, II),
                         d.diatonic(current_suit, V),
                         d.noteOfDegree(current_suit, V).stepBy(-1)),
      2));

    changes = changes.append(d.change(d.diatonic(current_suit, V), 4));
    return changes;
  }

//  private static Vector<JaCPDSL.Change> suit(
//    final JaCPDSL d,
//    Vector<JaCPDSL.Change> changes,
//    final JaCPDSL.Scale current_suit,
//    final JaCPDSL.Scale next_suit)
//  {
//    JaCPDSL.Degree degree = ofInt(moduloIndex(10));
//    for (int index = 0; index < 10; ++index) {
//      if (index == 9) {
//        changes = changes.append(
//          d.change(d.diatonic(current_suit, degree), 4));
//        changes = changes.append(
//          d.change(d.secondaryDominant(d.secondaryDominant(d.diatonic(next_suit, V))), 3));
//      } else {
//        changes = changes.append(
//          d.change(d.diatonic(current_suit, degree), 6));
//
//        changes = changes.append(
//          d.change(d.secondaryDominant(d.diatonic(current_suit, degree.previous())), 1));
//      }
//      degree = degree.previous();
//    }
//    return changes;
//  }

  private static int moduloIndex(final int index)
  {
    final int c_index;
    if (index > 7) {
      c_index = (index % 7) + 1;
    } else {
      c_index = index;
    }
    return c_index;
  }
}
