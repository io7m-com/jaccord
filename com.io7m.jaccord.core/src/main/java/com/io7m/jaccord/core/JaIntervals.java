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

import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.SortedSet;

/**
 * Interval values and functions.
 */

public final class JaIntervals
{
  /**
   * The unison interval.
   */

  public static final Integer UNISON = Integer.valueOf(0);

  /**
   * The minor second interval.
   */

  public static final Integer MINOR_SECOND = Integer.valueOf(1);

  /**
   * The major second interval.
   */

  public static final Integer MAJOR_SECOND = Integer.valueOf(2);

  /**
   * The minor third interval.
   */

  public static final Integer MINOR_THIRD = Integer.valueOf(3);

  /**
   * The major third interval.
   */

  public static final Integer MAJOR_THIRD = Integer.valueOf(4);

  /**
   * The perfect fourth interval.
   */

  public static final Integer FOURTH = Integer.valueOf(5);

  /**
   * The augmented fourth interval.
   */

  public static final Integer AUGMENTED_FOURTH = Integer.valueOf(6);

  /**
   * The diminished fifth interval.
   */

  public static final Integer DIMINISHED_FIFTH = AUGMENTED_FOURTH;

  /**
   * The perfect fifth interval.
   */

  public static final Integer FIFTH = Integer.valueOf(7);

  /**
   * The augmented fifth interval.
   */

  public static final Integer AUGMENTED_FIFTH = Integer.valueOf(8);

  /**
   * The minor sixth interval.
   */

  public static final Integer MINOR_SIXTH = AUGMENTED_FIFTH;

  /**
   * The major sixth interval.
   */

  public static final Integer MAJOR_SIXTH = Integer.valueOf(9);

  /**
   * The minor seventh interval.
   */

  public static final Integer MINOR_SEVENTH = Integer.valueOf(10);

  /**
   * The major seventh interval.
   */

  public static final Integer MAJOR_SEVENTH = Integer.valueOf(11);

  /**
   * The octave (unison) interval.
   */

  public static final Integer OCTAVE = Integer.valueOf(12);

  /**
   * The minor ninth interval.
   */

  public static final Integer MINOR_NINTH = Integer.valueOf(13);

  /**
   * The major ninth interval.
   */

  public static final Integer MAJOR_NINTH = Integer.valueOf(14);

  /**
   * The minor tenth interval.
   */

  public static final Integer MINOR_TENTH = Integer.valueOf(15);

  /**
   * The major tenth interval.
   */

  public static final Integer MAJOR_TENTH = Integer.valueOf(16);

  /**
   * The eleventh interval.
   */

  public static final Integer ELEVENTH = Integer.valueOf(17);

  /**
   * The augmented eleventh interval.
   */

  public static final Integer AUGMENTED_ELEVENTH = Integer.valueOf(18);

  /**
   * The tritave (octave plus fifth) interval.
   */

  public static final Integer TRITAVE = Integer.valueOf(19);

  /**
   * The minor thirteenth interval.
   */

  public static final Integer MINOR_THIRTEENTH = Integer.valueOf(20);

  /**
   * The major thirteenth interval.
   */

  public static final Integer MAJOR_THIRTEENTH = Integer.valueOf(21);

  /**
   * The minor fourteenth interval.
   */

  public static final Integer MINOR_FOURTEENTH = Integer.valueOf(22);

  /**
   * The major fourteenth interval.
   */

  public static final Integer MAJOR_FOURTEENTH = Integer.valueOf(23);

  /**
   * The double octave (unison) interval.
   */

  public static final Integer DOUBLE_OCTAVE = Integer.valueOf(24);

  private JaIntervals()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Normalize a set of intervals, removing unisons and intervals above a
   * two-octave cutoff point.
   *
   * @param intervals The intervals
   *
   * @return A normalized set of intervals
   */

  public static SortedSet<Integer> normalize(
    final SortedSet<Integer> intervals)
  {
    NullCheck.notNull(intervals, "intervals");
    return intervals.filter(i -> isWithinTwoOctaves(i) && isNotUnison(i));
  }

  private static boolean isNotUnison(
    final Integer i)
  {
    return i.intValue() != UNISON.intValue()
      && i.intValue() != OCTAVE.intValue()
      && i.intValue() != DOUBLE_OCTAVE.intValue();
  }

  private static boolean isWithinTwoOctaves(
    final Integer i)
  {
    return i.intValue() >= 0 && i.intValue() <= 24;
  }
}
