/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.io7m.jaccord.core.JaNote.B;
import static com.io7m.jaccord.core.JaNote.E;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.I;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.II;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.III;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.IV;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.V;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.VI;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.VII;

public final class JaCPDSLDemo8
{
  private JaCPDSLDemo8()
  {

  }

  public static void main(
    final String[] args)
    throws IOException, InvalidMidiDataException
  {
    final JaCPDSL d = JaCPDSL.create();

    final JaCPDSL.Scale base = d.scale(B, "Natural_Minor");
    final JaCPDSL.Scale flavour = d.scale(B, "Phrygian_Mode");
    final JaCPDSL.Scale passing = d.scale(B, "Harmonic_Minor");

    final JaCPDSL.Scale target = d.scale(E, "Natural_Minor");
    final JaCPDSL.Scale target_passing = d.scale(E, "Harmonic_Minor");

    final JaCPDSL.Progression p =
      d.progression(
        d.change(d.diatonic9(flavour, I), 4),
        d.change(d.diatonic7(flavour, VII), 3),
        d.change(d.diatonic9(flavour, I), 4),
        d.change(d.diatonic7(base, IV), 3),
        d.change(d.diatonic9(flavour, I), 4),
        d.change(d.diatonic7(flavour, VII), 3),
        d.change(d.diatonic7(base, IV), 7),

        d.change(d.diatonic9(flavour, I), 4),
        d.change(d.diatonic7(flavour, VII), 3),
        d.change(d.diatonic9(flavour, I), 4),
        d.change(d.diatonic7(base, III), 3),
        d.change(d.diatonic9(base, VI), 4),
        d.change(d.diatonic7(base, II), 3),
        d.change(d.diatonic7(passing, V), 5),
        d.change(d.diatonic7(passing, VII), 2),

        d.change(d.diatonic7(flavour, I), 4),
        d.change(d.secondaryDominant(d.diatonic7(target, V)), 1),
        d.change(d.tritoneSecondaryDominant(d.diatonic7(target, V)), 1),
        d.change(d.diatonic7(target_passing, V), 1),
        d.change(d.diatonic7(target, I), 7)
      );

    System.out.println(p);

    final JaCPDSLExporterConfiguration configuration =
      JaCPDSLExporterConfiguration.builder()
        .setDoubleRoot(true)
        .setOmitFifth(false)
        .build();

    try (final OutputStream out = Files.newOutputStream(Paths.get("/tmp/out.mid"))) {
      final Sequence sequence =
        JaCPDSLExporter.exportWithConfiguration(configuration, p);
      MidiSystem.write(sequence, 1, out);
    }
  }
}
