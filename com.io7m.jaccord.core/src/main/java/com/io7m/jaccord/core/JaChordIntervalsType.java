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

import io.vavr.collection.SortedSet;
import org.immutables.value.Value;

import java.util.stream.Collectors;

/**
 * A set of intervals that make up a chord.
 */

@JaImmutableStyleType
@Value.Immutable
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
   *
   * @throws JaExceptionChord If the intervals do not form a chord
   */

  @Value.Check
  default void checkPreconditions()
  {
    final SortedSet<Integer> norm = this.intervalsNormalized();
    if (norm.isEmpty()) {
      throw new JaExceptionChord(
        new StringBuilder(64)
          .append("Malformed chord intervals.")
          .append(System.lineSeparator())
          .append("  Expected: At least one non-unison interval")
          .append(System.lineSeparator())
          .append("  Received: ")
          .append(this.intervalsNormalized()
                    .map(Object::toString)
                    .collect(Collectors.joining(" ")))
          .append(System.lineSeparator())
          .toString());
    }

    final SortedSet<Integer> out_of_range =
      this.intervals().filter(i -> i.intValue() < 0 || i.intValue() > 24);
    if (!out_of_range.isEmpty()) {
      throw new JaExceptionChord(
        new StringBuilder(64)
          .append("Out-of-range chord intervals.")
          .append(System.lineSeparator())
          .append("  Expected: All intervals to be in the range [0, 24]")
          .append(System.lineSeparator())
          .append("  Received: ")
          .append(this.intervals()
                    .map(Object::toString)
                    .collect(Collectors.joining(" ")))
          .append(System.lineSeparator())
          .toString());
    }
  }
}
