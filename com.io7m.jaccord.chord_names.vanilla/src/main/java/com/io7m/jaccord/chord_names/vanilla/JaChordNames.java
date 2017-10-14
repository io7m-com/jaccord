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

package com.io7m.jaccord.chord_names.vanilla;

import com.io7m.jaccord.core.JaChordIntervals;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;

import static com.io7m.jaccord.core.JaIntervals.AUGMENTED_FIFTH;
import static com.io7m.jaccord.core.JaIntervals.DIMINISHED_FIFTH;
import static com.io7m.jaccord.core.JaIntervals.ELEVENTH;
import static com.io7m.jaccord.core.JaIntervals.FIFTH;
import static com.io7m.jaccord.core.JaIntervals.FOURTH;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_FOURTEENTH;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_NINTH;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_SECOND;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_SEVENTH;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_SIXTH;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_TENTH;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_THIRD;
import static com.io7m.jaccord.core.JaIntervals.MAJOR_THIRTEENTH;
import static com.io7m.jaccord.core.JaIntervals.MINOR_FOURTEENTH;
import static com.io7m.jaccord.core.JaIntervals.MINOR_NINTH;
import static com.io7m.jaccord.core.JaIntervals.MINOR_SEVENTH;
import static com.io7m.jaccord.core.JaIntervals.MINOR_TENTH;
import static com.io7m.jaccord.core.JaIntervals.MINOR_THIRD;
import static com.io7m.jaccord.core.JaIntervals.MINOR_THIRTEENTH;
import static com.io7m.jaccord.core.JaIntervals.TRITAVE;

/**
 * Functions to determine the names of chords.
 */

public final class JaChordNames
{
  private JaChordNames()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Attempt to name the given chord.
   *
   * @param chord The chord
   *
   * @return The chord name
   */

  public static String name(
    final JaChordIntervals chord)
  {
    NullCheck.notNull(chord, "Chord");

    final StringBuilder buffer = new StringBuilder(32);
    final SortedSet<Integer> notes = chord.intervalsNormalized();

    if (notes.isEmpty()) {
      throw new UnimplementedCodeException();
    }

    if (notes.contains(MINOR_THIRD)) {
      if (!notes.contains(FIFTH) && notes.contains(DIMINISHED_FIFTH)) {
        return nameDiminished(buffer, notes);
      }
      return nameMinor(buffer, notes);
    }

    if (notes.contains(MAJOR_THIRD)) {
      if (!notes.contains(FIFTH) && notes.contains(AUGMENTED_FIFTH)) {
        return nameAugmented(buffer, notes);
      }
      return nameMajor(buffer, notes);
    }

    return suspended(buffer, notes);
  }

  private static String suspended(
    final StringBuilder buffer,
    final SortedSet<Integer> notes)
  {
    if (notes.contains(FOURTH) || notes.contains(ELEVENTH)) {
      return nameSus("4", FOURTH, buffer, notes);
    }

    if (notes.contains(MAJOR_SECOND) || notes.contains(MAJOR_NINTH)) {
      return nameSus("2", MAJOR_SECOND, buffer, notes);
    }

    buffer.append("no3");
    if (!notes.contains(FIFTH)) {
      buffer.append("no5");
    }

    return addTones(buffer, FifthBehaviour.DO_NOT_IGNORE_FIFTHS, notes);
  }

  private static String nameSus(
    final String name,
    final Integer base,
    final StringBuilder buffer,
    final SortedSet<Integer> notes)
  {
    final TreeSet<Integer> remove = TreeSet.of(base);
    buffer.append("sus");
    buffer.append(name);
    return addTones(
      buffer,
      FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
      notes.removeAll(remove));
  }

  private static String nameAugmented(
    final StringBuilder buffer,
    final Set<Integer> notes)
  {
    final TreeSet<Integer> remove =
      TreeSet.of(MAJOR_THIRD, MAJOR_TENTH);

    buffer.append("aug");
    return addTones(
      buffer,
      FifthBehaviour.IGNORE_FIFTH,
      notes.removeAll(remove));
  }

  private static String nameDiminished(
    final StringBuilder buffer,
    final Set<Integer> notes)
  {
    TreeSet<Integer> remove =
      TreeSet.of(MINOR_THIRD, MINOR_TENTH);

    if (notes.contains(MAJOR_SIXTH)) {
      remove = remove.add(MAJOR_SIXTH);
      buffer.append("dim7");
      return addTones(
        buffer,
        FifthBehaviour.IGNORE_FIFTH,
        notes.removeAll(remove));
    }

    if (notes.contains(MINOR_SEVENTH)) {
      remove = remove.add(MINOR_SEVENTH);
      buffer.append("m7♭5");
      return addTones(
        buffer,
        FifthBehaviour.IGNORE_FIFTH,
        notes.removeAll(remove));
    }

    buffer.append("dim");
    return addTones(
      buffer,
      FifthBehaviour.IGNORE_FIFTH,
      notes.removeAll(remove));
  }

  private static String nameMajor(
    final StringBuilder buffer,
    final Set<Integer> notes)
  {
    TreeSet<Integer> remove =
      TreeSet.of(MAJOR_THIRD, MAJOR_TENTH);

    if (notes.contains(MINOR_SEVENTH) || notes.contains(MINOR_FOURTEENTH)) {
      return nameDominantSeventh(buffer, notes);
    }

    if (notes.contains(MAJOR_SEVENTH) || notes.contains(MAJOR_FOURTEENTH)) {
      return nameMajorSeventh(buffer, notes);
    }

    if (notes.contains(MAJOR_SIXTH)) {
      remove = remove.add(MAJOR_SIXTH);

      if (notes.contains(MAJOR_NINTH)) {
        remove = remove.add(MAJOR_NINTH);
        buffer.append("6/9");
        return addTones(
          buffer,
          FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
          notes.removeAll(remove));
      }

      buffer.append("6");
      return addTones(
        buffer,
        FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
        notes.removeAll(remove));
    }

    buffer.append("M");
    return addTones(
      buffer,
      FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
      notes.removeAll(remove));
  }

  private static String nameMajorSeventh(
    final StringBuilder buffer,
    final Set<Integer> notes)
  {
    TreeSet<Integer> remove =
      TreeSet.of(MAJOR_THIRD, MAJOR_TENTH,
                 MAJOR_SEVENTH, MAJOR_FOURTEENTH);

    if (notes.contains(MAJOR_NINTH)) {
      remove = remove.add(MAJOR_NINTH);

      if (notes.contains(ELEVENTH)) {
        remove = remove.add(ELEVENTH);

        if (notes.contains(MAJOR_THIRTEENTH)) {
          remove = remove.add(MAJOR_THIRTEENTH);
          buffer.append("M13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        if (notes.contains(MINOR_THIRTEENTH)) {
          remove = remove.add(MINOR_THIRTEENTH);
          buffer.append("M11♭13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        buffer.append("M11");
        return addTones(
          buffer,
          FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
          notes.removeAll(remove));
      }

      buffer.append("M9");
      return addTones(
        buffer,
        FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
        notes.removeAll(remove));
    }

    if (notes.contains(MINOR_NINTH)) {
      remove = remove.add(MINOR_NINTH);

      if (notes.contains(ELEVENTH)) {
        remove = remove.add(ELEVENTH);

        if (notes.contains(MAJOR_THIRTEENTH)) {
          remove = remove.add(MAJOR_THIRTEENTH);
          buffer.append("M13♭9");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        if (notes.contains(MINOR_THIRTEENTH)) {
          remove = remove.add(MINOR_THIRTEENTH);
          buffer.append("M11♭9♭13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        buffer.append("M11♭9");
        return addTones(
          buffer,
          FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
          notes.removeAll(remove));
      }

      buffer.append("M7♭9");
      return addTones(
        buffer,
        FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
        notes.removeAll(remove));
    }

    buffer.append("M7");
    return addTones(
      buffer,
      FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
      notes.removeAll(remove));
  }

  private static String nameDominantSeventh(
    final StringBuilder buffer,
    final Set<Integer> notes)
  {
    TreeSet<Integer> remove =
      TreeSet.of(MAJOR_THIRD, MAJOR_TENTH,
                 MINOR_SEVENTH, MINOR_FOURTEENTH);

    if (notes.contains(MAJOR_NINTH)) {
      remove = remove.add(MAJOR_NINTH);

      if (notes.contains(ELEVENTH)) {
        remove = remove.add(ELEVENTH);

        if (notes.contains(MAJOR_THIRTEENTH)) {
          remove = remove.add(MAJOR_THIRTEENTH);
          buffer.append("13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        if (notes.contains(MINOR_THIRTEENTH)) {
          remove = remove.add(MINOR_THIRTEENTH);
          buffer.append("11♭13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        buffer.append("11");
        return addTones(
          buffer,
          FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
          notes.removeAll(remove));
      }

      buffer.append("9");
      return addTones(
        buffer,
        FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
        notes.removeAll(remove));
    }

    if (notes.contains(MINOR_NINTH)) {
      remove = remove.add(MINOR_NINTH);

      if (notes.contains(ELEVENTH)) {
        remove = remove.add(ELEVENTH);

        if (notes.contains(MAJOR_THIRTEENTH)) {
          remove = remove.add(MAJOR_THIRTEENTH);
          buffer.append("13♭9");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        if (notes.contains(MINOR_THIRTEENTH)) {
          remove = remove.add(MINOR_THIRTEENTH);
          buffer.append("11♭9♭13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        buffer.append("11♭9");
        return addTones(
          buffer,
          FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
          notes.removeAll(remove));
      }

      buffer.append("7♭9");
      return addTones(
        buffer,
        FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
        notes.removeAll(remove));
    }

    buffer.append("7");
    return addTones(
      buffer,
      FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
      notes.removeAll(remove));
  }

  private static String nameMinor(
    final StringBuilder buffer,
    final Set<Integer> notes)
  {
    TreeSet<Integer> remove =
      TreeSet.of(MINOR_THIRD, MINOR_TENTH);

    if (notes.contains(MINOR_SEVENTH) || notes.contains(MINOR_FOURTEENTH)) {
      return nameMinorSeventh(buffer, notes);
    }

    if (notes.contains(MAJOR_SEVENTH) || notes.contains(MAJOR_FOURTEENTH)) {
      return nameMinorMajorSeventh(buffer, notes);
    }

    if (notes.contains(MAJOR_SIXTH)) {
      remove = remove.add(MAJOR_SIXTH);

      if (notes.contains(MAJOR_NINTH)) {
        remove = remove.add(MAJOR_NINTH);
        buffer.append("mM6/9");
        return addTones(
          buffer,
          FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
          notes.removeAll(remove));
      }

      buffer.append("mM6");
      return addTones(
        buffer,
        FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
        notes.removeAll(remove));
    }

    buffer.append("m");
    return addTones(
      buffer,
      FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
      notes.removeAll(remove));
  }

  private static String nameMinorMajorSeventh(
    final StringBuilder buffer,
    final Set<Integer> notes)
  {
    TreeSet<Integer> remove =
      TreeSet.of(MINOR_THIRD, MINOR_TENTH,
                 MAJOR_SEVENTH, MAJOR_FOURTEENTH);

    if (notes.contains(MAJOR_NINTH)) {
      remove = remove.add(MAJOR_NINTH);

      if (notes.contains(ELEVENTH)) {
        remove = remove.add(ELEVENTH);

        if (notes.contains(MAJOR_THIRTEENTH)) {
          remove = remove.add(MAJOR_THIRTEENTH);
          buffer.append("m13M7");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        if (notes.contains(MINOR_THIRTEENTH)) {
          remove = remove.add(MINOR_THIRTEENTH);
          buffer.append("m11M7♭13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        buffer.append("m11M7");
        return addTones(
          buffer,
          FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
          notes.removeAll(remove));
      }

      buffer.append("m9M7");
      return addTones(
        buffer,
        FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
        notes.removeAll(remove));
    }

    if (notes.contains(MINOR_NINTH)) {
      remove = remove.add(MINOR_NINTH);

      if (notes.contains(ELEVENTH)) {
        remove = remove.add(ELEVENTH);

        if (notes.contains(MAJOR_THIRTEENTH)) {
          remove = remove.add(MAJOR_THIRTEENTH);
          buffer.append("m13M7♭9");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        if (notes.contains(MINOR_THIRTEENTH)) {
          remove = remove.add(MINOR_THIRTEENTH);
          buffer.append("m11M7♭9♭13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        buffer.append("m11M7♭9");
        return addTones(
          buffer,
          FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
          notes.removeAll(remove));
      }

      buffer.append("mM7♭9");
      return addTones(
        buffer,
        FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
        notes.removeAll(remove));
    }

    buffer.append("mM7");
    return addTones(
      buffer,
      FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
      notes.removeAll(remove));
  }

  private static String nameMinorSeventh(
    final StringBuilder buffer,
    final Set<Integer> notes)
  {
    TreeSet<Integer> remove =
      TreeSet.of(MINOR_THIRD, MINOR_TENTH,
                 MINOR_SEVENTH, MINOR_FOURTEENTH);

    if (notes.contains(MAJOR_NINTH)) {
      remove = remove.add(MAJOR_NINTH);

      if (notes.contains(ELEVENTH)) {
        remove = remove.add(ELEVENTH);

        if (notes.contains(MAJOR_THIRTEENTH)) {
          remove = remove.add(MAJOR_THIRTEENTH);
          buffer.append("m13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        if (notes.contains(MINOR_THIRTEENTH)) {
          remove = remove.add(MINOR_THIRTEENTH);
          buffer.append("m11♭13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        buffer.append("m11");
        return addTones(
          buffer,
          FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
          notes.removeAll(remove));
      }

      buffer.append("m9");
      return addTones(
        buffer,
        FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
        notes.removeAll(remove));
    }

    if (notes.contains(MINOR_NINTH)) {
      remove = remove.add(MINOR_NINTH);

      if (notes.contains(ELEVENTH)) {
        remove = remove.add(ELEVENTH);

        if (notes.contains(MAJOR_THIRTEENTH)) {
          remove = remove.add(MAJOR_THIRTEENTH);
          buffer.append("m13♭9");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        if (notes.contains(MINOR_THIRTEENTH)) {
          remove = remove.add(MINOR_THIRTEENTH);
          buffer.append("m11♭9♭13");
          return addTones(
            buffer,
            FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
            notes.removeAll(remove));
        }

        buffer.append("m11♭9");
        return addTones(
          buffer,
          FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
          notes.removeAll(remove));
      }

      buffer.append("m7♭9");
      return addTones(
        buffer,
        FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
        notes.removeAll(remove));
    }

    buffer.append("m7");
    return addTones(
      buffer,
      FifthBehaviour.DO_NOT_IGNORE_FIFTHS,
      notes.removeAll(remove));
  }

  private enum FifthBehaviour
  {
    IGNORE_FIFTH,
    DO_NOT_IGNORE_FIFTHS
  }

  private static String addTones(
    final StringBuilder buffer,
    final FifthBehaviour fifths,
    final Set<Integer> intervals)
  {
    Set<Integer> add_intervals = intervals;
    switch (fifths) {
      case IGNORE_FIFTH: {
        add_intervals = intervals.removeAll(
          TreeSet.of(FIFTH, TRITAVE, DIMINISHED_FIFTH, AUGMENTED_FIFTH));
        break;
      }
      case DO_NOT_IGNORE_FIFTHS: {
        add_intervals = intervals;
        break;
      }
    }

    add_intervals.forEach(i -> buffer.append(addTone(i.intValue())));
    return buffer.toString().trim();
  }

  private static String addTone(
    final int interval)
  {
    switch (interval) {
      case 0:
        return "";
      case 1:
        return "add♭2";
      case 2:
        return "add2";
      case 3:
        return "";
      case 4:
        return "";
      case 5:
        return "add4";
      case 6:
        return "add♯4";
      case 7:
        return "";
      case 8:
        return "add♭6";
      case 9:
        return "add6";
      case 10:
        return "add♭7";
      case 11:
        return "add7";
      case 12:
        return "";
      case 13:
        return "add♭9";
      case 14:
        return "add9";
      case 15:
        return "add♯9";
      case 16:
        return "add♭11";
      case 17:
        return "add11";
      case 18:
        return "add♯11";
      case 19:
        return "";
      case 20:
        return "add♭13";
      case 21:
        return "add13";
      case 22:
        return "add♭14";
      case 23:
        return "add14";
      case 24:
        return "";
      default:
        return "";
    }
  }
}
