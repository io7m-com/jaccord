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

import javax.sound.midi.MidiSystem;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.io7m.jaccord.core.JaNote.F;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.I;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.II;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.V;

public final class JaCPDSLDemo
{
  private JaCPDSLDemo()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    final JaCPDSL d = JaCPDSL.create();
    final JaCPDSL.Scale minor = d.scale(F, "Natural_Minor");

    final JaCPDSL.Progression p =
      d.progression(
        d.change(d.diatonic(minor, I), 4),
        d.change(d.diatonic(minor, II), 4),
        d.change(d.diatonic(minor, V), 4),
        d.change(d.diatonic(minor, I), 8));

    System.out.println(p);

    try (final OutputStream out = Files.newOutputStream(Paths.get("output.mid"))) {
      MidiSystem.write(JaCPDSLExporter.export(p), 1, out);
    }
  }
}
