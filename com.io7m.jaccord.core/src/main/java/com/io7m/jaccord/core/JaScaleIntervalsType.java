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
 * The type of scale intervals.
 */

@ImmutablesStyleType
@Immutable
public interface JaScaleIntervalsType
{
  /**
   * @return The intervals of the scale relative to the root note
   */

  @Value.Parameter
  SortedSet<Integer> intervals();

  /**
   * Check that all intervals are non-zero and less than 12.
   */

  @Value.Check
  default void checkPreconditions()
  {
    final SortedSet<Integer> is = this.intervals();

    try {
      checkIntervalsValid(is);
    } catch (final JaExceptionScaleInvalid e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Check that a given set of intervals is a valid scale. That is, all intervals
   * are non-zero and less than 12.
   *
   * @param intervals The intervals
   *
   * @throws JaExceptionScaleInvalid If the intervals do not form a scale
   */

  static void checkIntervalsValid(final SortedSet<Integer> intervals)
    throws JaExceptionScaleInvalid
  {
    Objects.requireNonNull(intervals, "Intervals");

    if (intervals.exists(i -> i.intValue() < 1 || i.intValue() >= 12)) {
      final String line_separator = System.lineSeparator();
      throw new JaExceptionScaleInvalid(
        new StringBuilder(64)
          .append("Scale validity error.")
          .append(line_separator)
          .append("  Expected: All intervals must be in the range [1, 11]")
          .append(line_separator)
          .append("  Received: ")
          .append(intervals.map(Object::toString).collect(Collectors.joining(" ")))
          .append(line_separator)
          .toString());
    }
  }
}
