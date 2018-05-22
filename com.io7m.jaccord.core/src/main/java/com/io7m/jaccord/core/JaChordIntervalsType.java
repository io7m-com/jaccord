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

import com.io7m.immutables.styles.ImmutablesStyleType;
import io.vavr.collection.SortedSet;
import org.immutables.value.Value;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.immutables.value.Value.Immutable;

/**
 * A set of intervals that make up a chord.
 */

@ImmutablesStyleType
@Immutable
public interface JaChordIntervalsType
{
  /**
   * @return The set of intervals
   */

  @Value.Parameter
  SortedSet<Integer> intervals();

  /**
   * @return The set of intervals without unisons
   */

  @Value.Derived
  default SortedSet<Integer> intervalsNormalized()
  {
    return JaIntervals.normalize(this.intervals());
  }

  /**
   * Check preconditions for the type
   */

  @Value.Check
  default void checkPreconditions()
  {
    final SortedSet<Integer> intervals = this.intervals();

    try {
      checkValidChord(intervals);
    } catch (final JaExceptionChordInvalid e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Check that a given set of intervals form a valid chord.
   *
   * @param intervals The intervals
   *
   * @throws JaExceptionChordInvalid If the intervals do not form a chord
   */

  static void checkValidChord(final SortedSet<Integer> intervals)
    throws JaExceptionChordInvalid
  {
    Objects.requireNonNull(intervals, "Intervals");

    final SortedSet<Integer> norm = JaIntervals.normalize(intervals);
    final SortedSet<Integer> out_of_range =
      intervals.filter(i -> i.intValue() < 0 || i.intValue() > 24);

    final String line_separator = System.lineSeparator();
    if (norm.isEmpty()) {
      throw new JaExceptionChordInvalid(
        new StringBuilder(64)
          .append("Malformed chord intervals.")
          .append(line_separator)
          .append("  Expected: At least one non-unison interval")
          .append(line_separator)
          .append("  Received: Nothing")
          .append(line_separator)
          .toString());
    }

    if (!out_of_range.isEmpty()) {
      throw new JaExceptionChordInvalid(
        new StringBuilder(64)
          .append("Out-of-range chord intervals.")
          .append(line_separator)
          .append("  Expected: All intervals to be in the range [0, 24]")
          .append(line_separator)
          .append("  Received: ")
          .append(intervals
                    .map(Object::toString)
                    .collect(Collectors.joining(" ")))
          .append(line_separator)
          .toString());
    }
  }
}
