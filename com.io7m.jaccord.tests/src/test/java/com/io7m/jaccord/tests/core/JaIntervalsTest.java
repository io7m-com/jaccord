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

package com.io7m.jaccord.tests.core;

import com.io7m.jaccord.core.JaIntervals;
import io.vavr.collection.SortedSet;
import io.vavr.collection.Stream;
import io.vavr.collection.TreeSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public final class JaIntervalsTest
{
  @Test
  public void testNormalize()
  {
    final SortedSet<Integer> x =
      JaIntervals.normalize(TreeSet.ofAll(Stream.range(-100, 100)));

    final java.util.stream.Stream<Executable> executables =
      Stream.range(-100, 100)
        .map(i -> (Executable) () -> {
          int ix = i.intValue();
          if (ix >= 0 && ix <= 24 && ix != 12 && ix != 24 && ix != 0) {
            Assertions.assertTrue(x.contains(i), "Contains " + i);
          } else {
            Assertions.assertFalse(x.contains(i), "Does not contain " + i);
          }
        }).toJavaStream();

    Assertions.assertAll(executables);
  }
}
