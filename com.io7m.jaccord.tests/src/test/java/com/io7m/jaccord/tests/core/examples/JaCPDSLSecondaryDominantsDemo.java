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
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.cpdsl.JaCPDSL;
import com.io7m.jaccord.cpdsl.midi.JaCPDSLExporter;
import com.io7m.jaccord.cpdsl.midi.JaCPDSLExporterConfiguration;
import io.vavr.collection.TreeMap;
import io.vavr.collection.TreeSet;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.io7m.jaccord.core.JaNote.E;
import static com.io7m.jaccord.core.JaNote.F;
import static com.io7m.jaccord.core.JaNote.G_SHARP;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.I;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.II;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.IV;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.V;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.VII;

public final class JaCPDSLSecondaryDominantsDemo
{
  private JaCPDSLSecondaryDominantsDemo()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    final JaCPDSL d = JaCPDSL.create();

    final JaCPDSL.Scale base = d.scale(F, "Natural_Minor");
    for (final JaCPDSL.Degree degree : JaCPDSL.Degree.values()) {
      System.out.println(d.diatonic7(base, degree));
      System.out.print("  ");
      System.out.println(d.secondaryDominant(d.diatonic7(base, degree)));
    }

    System.out.println();

    final JaCPDSL.Scale relative = d.scale(G_SHARP, "Major");
    for (final JaCPDSL.Degree degree : JaCPDSL.Degree.values()) {
      System.out.println(d.diatonic7(relative, degree));
      System.out.print("  ");
      System.out.println(d.secondaryDominant(d.diatonic7(relative, degree)));
    }
  }
}
