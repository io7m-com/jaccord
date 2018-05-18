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

package com.io7m.jaccord.core;

import java.util.Objects;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.SortedSet;
import io.vavr.collection.Vector;

/**
 * Functions to calculate inversions of chords.
 */

public final class JaChordInversions
{
  private JaChordInversions()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Invert the given chord.
   *
   * @param chord A chord
   *
   * @return The chord in first inversion
   */

  public static JaChord invert(
    final JaChord chord)
  {
    Objects.requireNonNull(chord, "Chord");

    final SortedSet<Integer> intervals = chord.intervals().intervals();
    final Integer first = intervals.head();
    final JaNote new_root = chord.root().stepBy(first.intValue());

    final SortedSet<Integer> new_intervals =
      intervals
        .tail()
        .add(nextLargerThanMax(intervals.max().get().intValue(), 12))
        .map(i -> Integer.valueOf(i.intValue() - first.intValue()));

    return JaChord.of(new_root, JaChordIntervals.of(new_intervals));
  }

  private static Integer nextLargerThanMax(
    final int i,
    final int max)
  {
    final int f = (int) Math.ceil((double) i / (double) max);
    final int v = f * max;
    return Integer.valueOf(v);
  }

  /**
   * Calculate all possible inversions of a given chord.
   *
   * @param chord A chord
   *
   * @return All possible inversions of the given chord
   */

  public static Vector<JaChord> inversions(
    final JaChord chord)
  {
    Objects.requireNonNull(chord, "Chord");

    Vector<JaChord> inversions = Vector.empty();
    JaChord inversion = chord;
    final SortedSet<Integer> intervals = chord.intervals().intervals();
    for (int index = 0; index < intervals.size(); ++index) {
      inversion = invert(inversion);
      inversions = inversions.append(inversion);
    }
    return inversions;
  }
}
