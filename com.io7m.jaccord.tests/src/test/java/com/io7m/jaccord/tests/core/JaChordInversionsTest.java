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

import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordIntervals;
import com.io7m.jaccord.core.JaChordInversions;
import com.io7m.jaccord.core.JaChordNames;
import com.io7m.jaccord.core.JaNote;
import io.vavr.collection.TreeSet;
import io.vavr.collection.Vector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public final class JaChordInversionsTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JaChordInversionsTest.class);

  @Test
  public void testInversion()
  {
    final JaChord chord =
      JaChord.of(
        JaNote.C,
        JaChordIntervals.of(TreeSet.of(
          Integer.valueOf(4),
          Integer.valueOf(7),
          Integer.valueOf(11))));

    final JaChord chord_inv_1 = JaChordInversions.invert(chord);
    final JaChord chord_inv_2 = JaChordInversions.invert(chord_inv_1);
    final JaChord chord_inv_3 = JaChordInversions.invert(chord_inv_2);
    final JaChord chord_inv_4 = JaChordInversions.invert(chord_inv_3);

    dumpChord(chord);
    dumpChord(chord_inv_1);
    dumpChord(chord_inv_2);
    dumpChord(chord_inv_3);
    dumpChord(chord_inv_4);

    Assertions.assertAll(
      () -> Assertions.assertEquals(
        Vector.of(JaNote.C, JaNote.E, JaNote.G, JaNote.B),
        chord.notes()),
      () -> Assertions.assertEquals(
        Vector.of(JaNote.E, JaNote.G, JaNote.B, JaNote.C),
        chord_inv_1.notes()),
      () -> Assertions.assertEquals(
        Vector.of(JaNote.G, JaNote.B, JaNote.C, JaNote.E),
        chord_inv_2.notes()),
      () -> Assertions.assertEquals(
        Vector.of(JaNote.B, JaNote.C, JaNote.E, JaNote.G),
        chord_inv_3.notes()),
      () -> Assertions.assertEquals(chord, chord_inv_4));
  }

  private static void dumpChord(
    final JaChord chord)
  {
    LOG.debug(
      "{}{} ({})",
      chord.root(),
      JaChordNames.name(chord.intervals()),
      chord.notes().map(JaNote::noteName).collect(Collectors.joining(" ")));
  }

  @Test
  public void testInversions()
  {
    final JaChord chord =
      JaChord.of(
        JaNote.C,
        JaChordIntervals.of(TreeSet.of(
          Integer.valueOf(4),
          Integer.valueOf(7),
          Integer.valueOf(11))));

    final JaChord chord_inv_1 = JaChordInversions.invert(chord);
    final JaChord chord_inv_2 = JaChordInversions.invert(chord_inv_1);
    final JaChord chord_inv_3 = JaChordInversions.invert(chord_inv_2);

    dumpChord(chord);
    dumpChord(chord_inv_1);
    dumpChord(chord_inv_2);
    dumpChord(chord_inv_3);

    final Vector<JaChord> inversions = JaChordInversions.inversions(chord);

    inversions.forEach(JaChordInversionsTest::dumpChord);

    Assertions.assertAll(
      () -> Assertions.assertEquals(3, inversions.size()),
      () -> Assertions.assertEquals(chord_inv_1, inversions.get(0)),
      () -> Assertions.assertEquals(chord_inv_2, inversions.get(1)),
      () -> Assertions.assertEquals(chord_inv_3, inversions.get(2)));
  }

  @Test
  public void testBug1()
  {
    final JaChord chord =
      JaChord.of(
        JaNote.E,
        JaChordIntervals.of(TreeSet.of(
          Integer.valueOf(3),
          Integer.valueOf(10),
          Integer.valueOf(20))));

    System.out.println(JaChordNames.name(chord.intervals()));

    final JaChord chord_inv_1 = JaChordInversions.invert(chord);
    final JaChord chord_inv_2 = JaChordInversions.invert(chord_inv_1);
    final JaChord chord_inv_3 = JaChordInversions.invert(chord_inv_2);
    final JaChord chord_inv_4 = JaChordInversions.invert(chord_inv_3);

    dumpChord(chord);
    dumpChord(chord_inv_1);
    dumpChord(chord_inv_2);
    dumpChord(chord_inv_3);
    dumpChord(chord_inv_4);

    Assertions.assertAll(
      () -> Assertions.assertEquals(
        Vector.of(JaNote.E, JaNote.G, JaNote.D, JaNote.C),
        chord.notes()),
      () -> Assertions.assertEquals(
        Vector.of(JaNote.G, JaNote.D, JaNote.C, JaNote.E),
        chord_inv_1.notes()),
      () -> Assertions.assertEquals(
        Vector.of(JaNote.D, JaNote.C, JaNote.E, JaNote.G),
        chord_inv_2.notes()),
      () -> Assertions.assertEquals(
        Vector.of(JaNote.C, JaNote.E, JaNote.G, JaNote.D),
        chord_inv_3.notes()),
      () -> Assertions.assertEquals(chord, chord_inv_4));
  }
}
