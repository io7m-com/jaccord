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

import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.SortedSet;

import java.util.Objects;
import java.util.Optional;

/**
 * Functions to restate a given chord as a slash chord.
 */

public final class JaChordSlashes
{
  private JaChordSlashes()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Restate a given chord as a slash chord.
   *
   * @param chord A chord
   *
   * @return A slash chord, or nothing if there are too few notes to form a
   * chord
   *
   * @throws JaExceptionChord If the given slash chord is malformed
   */

  public static Optional<JaChordSlash> slash(
    final JaChord chord)
    throws JaExceptionChord
  {
    Objects.requireNonNull(chord, "Chord");

    final SortedSet<Integer> intervals = chord.intervals().intervals();
    final Integer first = intervals.head();

    final JaNote new_root =
      chord.root().stepBy(first.intValue());

    final SortedSet<Integer> new_intervals =
      JaIntervals.normalize(
        intervals.tail()
          .map(i -> Integer.valueOf(i.intValue() - first.intValue())));

    if (new_intervals.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(
      JaChordSlash.of(
        chord.root(),
        JaChord.of(new_root, JaChordIntervals.of(new_intervals))));
  }
}
