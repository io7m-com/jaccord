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

package com.io7m.jaccord.tests.core;

import com.io7m.jaccord.cpdsl.JaCPDSL;

import static com.io7m.jaccord.core.JaNote.E;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.I;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.II;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.IV;
import static com.io7m.jaccord.cpdsl.JaCPDSL.Degree.V;

public final class JaCPDSLTest
{
  private JaCPDSLTest()
  {

  }

  public static void main(
    final String[] args)
  {
    final JaCPDSL d = JaCPDSL.create();
    final JaCPDSL.Scale minor = d.scale(E, "Natural_Minor");

    System.out.println(
      d.progression(
        d.change(d.diatonic9(minor, I), 5),
        d.change(d.diatonic9(minor, II), 5),
        d.change(d.diatonic9(minor, I), 5),
        d.change(d.diatonic9(minor, IV), 5),
        d.change(d.diatonic9(minor, V), 10)
      )
    );
  }
}
