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

import com.io7m.jaccord.core.JaIntervals;
import com.io7m.jaccord.cpdsl.JaCPDSL;
import com.io7m.jaccord.cpdsl.midi.JaCPDSLExporter;
import com.io7m.jaccord.cpdsl.midi.JaCPDSLExporterConfiguration;
import io.vavr.collection.TreeMap;
import io.vavr.collection.TreeSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.io7m.jaccord.core.JaNote.A;
import static com.io7m.jaccord.core.JaNote.D;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.I;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.II;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.III;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.IV;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.V;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.VI;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.VII;

public final class JaCPDSLDemo3
{
  private JaCPDSLDemo3()
  {

  }

  public static void main(
    final String[] args)
    throws IOException, InvalidMidiDataException
  {
    final JaCPDSL d = JaCPDSL.create();

    final JaCPDSL.Scale base = d.scale(A, "Major");
    final JaCPDSL.Scale target = d.scale(D, "Natural_Minor");

    final JaCPDSL.Progression p =
      d.progression(
        d.change(d.diatonic(base, I), 7),
        d.change(d.chromaticTranspose(d.secondaryDominant(d.diatonic7(base, I)), 6), 1),

        d.change(d.diatonic7(base, II), 7),
        d.change(d.chromaticTranspose(d.secondaryDominant(d.diatonic7(base, II)), 6), 1),

        d.change(d.diatonic9(base, III), 7),
        d.change(d.chromaticTranspose(d.secondaryDominant(d.diatonic7(base, III)), 6), 1),

        d.change(d.diatonic(base, VI), 7),
        d.change(d.chromaticTranspose(d.secondaryDominant(d.diatonic7(base, VII)), 6), 1),

        d.change(d.diatonic7(target, V), 4),
        d.change(d.diatonic(target, I), 4),
        d.change(d.diatonic7(target, V), 2),
        d.change(d.diatonic9(target, VI), 2),
        d.change(d.diatonic(target, VII), 2),
        d.change(
          d.chromaticTranspose(d.diatonic7(target, VII), 1),
          2)
      );

    System.out.println(p);

    final JaCPDSLExporterConfiguration configuration =
      JaCPDSLExporterConfiguration.builder()
        .setDoubleRoot(true)
        .setOmitFifth(true)
        .build();

    try (final OutputStream out = Files.newOutputStream(Paths.get("output_rising.mid"))) {
      final Sequence sequence =
        JaCPDSLExporter.exportWithConfiguration(configuration, p);
      MidiSystem.write(sequence, 1, out);
    }
  }
}
