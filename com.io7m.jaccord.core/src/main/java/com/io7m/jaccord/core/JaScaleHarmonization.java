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

package com.io7m.jaccord.core;

import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.List;
import io.vavr.collection.TreeSet;
import io.vavr.collection.Vector;

import java.util.Objects;

import static com.io7m.jaccord.core.JaIntervals.AUGMENTED_FIFTH;
import static com.io7m.jaccord.core.JaIntervals.AUGMENTED_FOURTH;
import static com.io7m.jaccord.core.JaIntervals.DIMINISHED_FIFTH;
import static com.io7m.jaccord.core.JaIntervals.FIFTH;
import static com.io7m.jaccord.core.JaIntervals.FOURTH;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_SECOND;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_SIXTH;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_THIRD;
import static com.io7m.jaccord.core.JaIntervals.MINOR_SECOND;
import static com.io7m.jaccord.core.JaIntervals.MINOR_SIXTH;
import static com.io7m.jaccord.core.JaIntervals.MINOR_THIRD;

/**
 * Functions to harmonize scales.
 */

public final class JaScaleHarmonization
{
  private JaScaleHarmonization()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Harmonize the given scale.
   *
   * @param types The type of chords returned
   * @param scale The scale
   *
   * @return The chords that can be constructed from the scale
   */

  public static Vector<JaChord> harmonize(
    final JaScaleHarmonizationChordTypes types,
    final JaScale scale)
  {
    Objects.requireNonNull(types, "Types");
    Objects.requireNonNull(scale, "Scale");
    return scale.notesOrdered().map(note -> harmonizeNote(types, scale, note));
  }

  private static JaChord harmonizeNote(
    final JaScaleHarmonizationChordTypes types,
    final JaScale scale,
    final JaNote root)
  {
    Objects.requireNonNull(types, "Types");
    Objects.requireNonNull(scale, "Scale");
    Objects.requireNonNull(root, "Note");

    switch (types) {
      case SUSPENDED_2_CHORDS:
        return harmonizeSus2(scale, root);

      case SUSPENDED_4_CHORDS:
        return harmonizeSus4(scale, root);

      case TRIADS:
        return harmonizeTriads(scale, root);

      case SIXTH_CHORDS:
        return harmonizeSixths(scale, root);

      case SEVENTH_CHORDS:
        return harmonizeSevenths(scale, root);

      case NINTH_CHORDS:
        return harmonizeNinths(scale, root);

      case ELEVENTH_CHORDS:
        return harmonizeElevenths(scale, root);

      case THIRTEENTH_CHORDS:
        return harmonizeThirteenths(scale, root);
    }

    throw new UnreachableCodeException();
  }

  private static JaChord harmonizeSixths(
    final JaScale scale,
    final JaNote root)
  {
    final JaNote third =
      findNext(root, List.of(MAJOR_THIRD, MINOR_THIRD), scale);
    final JaNote five =
      findNext(root, List.of(FIFTH, DIMINISHED_FIFTH, AUGMENTED_FIFTH), scale);
    final JaNote six =
      findNext(root, List.of(MAJOR_SIXTH, MINOR_SIXTH), scale);

    TreeSet<Integer> intervals = TreeSet.empty();
    intervals = intervals.add(Integer.valueOf(root.intervalUpTo(third)));
    intervals = intervals.add(Integer.valueOf(root.intervalUpTo(five)));
    intervals = intervals.add(Integer.valueOf(root.intervalUpTo(six)));

    return JaChord.of(root, JaChordIntervals.of(intervals));
  }

  private static JaChord harmonizeSus2(
    final JaScale scale,
    final JaNote root)
  {
    final JaNote two =
      findNext(root, List.of(MAJOR_SECOND, MINOR_SECOND), scale);
    final JaNote five =
      findNext(root, List.of(FIFTH, DIMINISHED_FIFTH, AUGMENTED_FIFTH), scale);

    TreeSet<Integer> intervals = TreeSet.empty();
    intervals = intervals.add(Integer.valueOf(root.intervalUpTo(two)));
    intervals = intervals.add(Integer.valueOf(root.intervalUpTo(five)));

    return JaChord.of(root, JaChordIntervals.of(intervals));
  }

  private static JaChord harmonizeSus4(
    final JaScale scale,
    final JaNote root)
  {
    final JaNote four =
      findNext(root, List.of(FOURTH, AUGMENTED_FOURTH), scale);
    final JaNote five =
      findNext(root, List.of(FIFTH, DIMINISHED_FIFTH, AUGMENTED_FIFTH), scale);

    TreeSet<Integer> intervals = TreeSet.empty();
    intervals = intervals.add(Integer.valueOf(root.intervalUpTo(four)));
    intervals = intervals.add(Integer.valueOf(root.intervalUpTo(five)));

    return JaChord.of(root, JaChordIntervals.of(intervals));
  }

  private static JaChord harmonizeTriads(
    final JaScale scale,
    final JaNote root)
  {
    final JaNote three =
      findNext(root, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote five =
      findNext(three, List.of(MINOR_THIRD, MAJOR_THIRD), scale);

    TreeSet<Integer> intervals = TreeSet.empty();
    int interval = root.intervalUpTo(three);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += three.intervalUpTo(five);
    intervals = intervals.add(Integer.valueOf(interval));

    return JaChord.of(root, JaChordIntervals.of(intervals));
  }

  private static JaChord harmonizeSevenths(
    final JaScale scale,
    final JaNote root)
  {
    final JaNote three =
      findNext(root, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote five =
      findNext(three, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote seven =
      findNext(five, List.of(MINOR_THIRD, MAJOR_THIRD), scale);

    TreeSet<Integer> intervals = TreeSet.empty();
    int interval = root.intervalUpTo(three);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += three.intervalUpTo(five);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += five.intervalUpTo(seven);
    intervals = intervals.add(Integer.valueOf(interval));

    return JaChord.of(root, JaChordIntervals.of(intervals));
  }

  private static JaChord harmonizeNinths(
    final JaScale scale,
    final JaNote root)
  {
    final JaNote three =
      findNext(root, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote five =
      findNext(three, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote seven =
      findNext(five, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote nine =
      findNext(seven, List.of(MINOR_THIRD, MAJOR_THIRD), scale);

    TreeSet<Integer> intervals = TreeSet.empty();
    int interval = root.intervalUpTo(three);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += three.intervalUpTo(five);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += five.intervalUpTo(seven);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += seven.intervalUpTo(nine);
    intervals = intervals.add(Integer.valueOf(interval));

    return JaChord.of(root, JaChordIntervals.of(intervals));
  }

  private static JaChord harmonizeElevenths(
    final JaScale scale,
    final JaNote root)
  {
    final JaNote three =
      findNext(root, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote five =
      findNext(three, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote seven =
      findNext(five, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote nine =
      findNext(seven, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote eleven =
      findNext(nine, List.of(MINOR_THIRD, MAJOR_THIRD), scale);

    TreeSet<Integer> intervals = TreeSet.empty();
    int interval = root.intervalUpTo(three);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += three.intervalUpTo(five);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += five.intervalUpTo(seven);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += seven.intervalUpTo(nine);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += nine.intervalUpTo(eleven);
    intervals = intervals.add(Integer.valueOf(interval));

    return JaChord.of(root, JaChordIntervals.of(intervals));
  }

  private static JaChord harmonizeThirteenths(
    final JaScale scale,
    final JaNote root)
  {
    final JaNote three =
      findNext(root, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote five =
      findNext(three, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote seven =
      findNext(five, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote nine =
      findNext(seven, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote eleven =
      findNext(nine, List.of(MINOR_THIRD, MAJOR_THIRD), scale);
    final JaNote thirteen =
      findNext(eleven, List.of(MINOR_THIRD, MAJOR_THIRD), scale);

    TreeSet<Integer> intervals = TreeSet.empty();
    int interval = root.intervalUpTo(three);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += three.intervalUpTo(five);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += five.intervalUpTo(seven);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += seven.intervalUpTo(nine);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += nine.intervalUpTo(eleven);
    intervals = intervals.add(Integer.valueOf(interval));
    interval += eleven.intervalUpTo(thirteen);
    intervals = intervals.add(Integer.valueOf(interval));

    return JaChord.of(root, JaChordIntervals.of(intervals));
  }

  private static JaNote findNext(
    final JaNote note,
    final List<Integer> intervals,
    final JaScale scale)
  {
    for (int index = 0; index < intervals.size(); ++index) {
      final Integer interval = intervals.get(index);
      final JaNote next = note.stepBy(interval.intValue());
      if (scale.notes().contains(next)) {
        return next;
      }
    }

    return findAnyNextNote(note, scale);
  }

  private static JaNote findAnyNextNote(
    final JaNote note,
    final JaScale scale)
  {
    JaNote current = note.next().next();
    while (true) {
      if (scale.notes().contains(current)) {
        return current;
      }
      current = current.next();
    }
  }
}
