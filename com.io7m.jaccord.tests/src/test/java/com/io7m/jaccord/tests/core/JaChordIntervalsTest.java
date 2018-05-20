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

package com.io7m.jaccord.tests.core;

import com.io7m.jaccord.core.JaChordIntervals;
import com.io7m.jaccord.core.JaExceptionChord;
import com.io7m.jaccord.core.JaExceptionChordInvalid;
import io.vavr.collection.TreeSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JaChordIntervalsTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JaChordIntervalsTest.class);

  @Test
  public void testOutOfRange()
  {
    final IllegalArgumentException ex =
      Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          JaChordIntervals.of(
            TreeSet.of(
              Integer.valueOf(4),
              Integer.valueOf(7),
              Integer.valueOf(25)));
        });

    Assertions.assertTrue(
      ex.getCause() instanceof JaExceptionChordInvalid);
    Assertions.assertTrue(
      ex.getMessage().contains("Out-of-range chord intervals"));
  }

  @Test
  public void testMalformedChord()
  {
    final IllegalArgumentException ex =
      Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          JaChordIntervals.of(
            TreeSet.of(
              Integer.valueOf(12)));
        });

    Assertions.assertTrue(
      ex.getCause() instanceof JaExceptionChordInvalid);
    Assertions.assertTrue(
      ex.getMessage().contains("Malformed chord intervals"));
  }
}
