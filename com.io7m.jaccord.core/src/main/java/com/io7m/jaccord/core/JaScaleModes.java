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
import io.vavr.collection.Set;
import io.vavr.collection.TreeSet;
import io.vavr.collection.Vector;

import java.util.Objects;

/**
 * Functions to calculate modes from a given scale.
 */

public final class JaScaleModes
{
  private JaScaleModes()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Calculate all modes of the given scale.
   *
   * @param scale The scale
   *
   * @return The modes of the scale
   */

  public static Vector<JaScale> modes(
    final JaScale scale)
  {
    Objects.requireNonNull(scale, "Scale");
    return scale.notesOrdered().tail().map(note -> buildMode(scale, note));
  }

  private static JaScale buildMode(
    final JaScale scale,
    final JaNote root)
  {
    TreeSet<Integer> intervals = TreeSet.empty();
    JaNote current = root;
    final Set<JaNote> nodes = scale.notes();
    for (int index = 0; index < nodes.size(); ++index) {
      while (true) {
        current = current.next();
        if (nodes.contains(current)) {
          final Integer interval = Integer.valueOf(root.intervalUpTo(current));
          if (interval.intValue() > 0) {
            intervals = intervals.add(interval);
          }
          break;
        }
      }
    }

    return JaScale.of(root, JaScaleIntervals.of(intervals));
  }
}
