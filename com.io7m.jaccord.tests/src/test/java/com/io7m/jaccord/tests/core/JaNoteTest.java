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

import com.io7m.jaccord.core.JaNote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class JaNoteTest
{
  @Test
  public void testIntervalUpTo()
  {
    Assertions.assertAll(
      () -> Assertions.assertEquals(0, JaNote.C.intervalUpTo(JaNote.C)),
      () -> Assertions.assertEquals(1, JaNote.C.intervalUpTo(JaNote.C_SHARP)),
      () -> Assertions.assertEquals(2, JaNote.C.intervalUpTo(JaNote.D)),
      () -> Assertions.assertEquals(3, JaNote.C.intervalUpTo(JaNote.D_SHARP)),
      () -> Assertions.assertEquals(4, JaNote.C.intervalUpTo(JaNote.E)),
      () -> Assertions.assertEquals(5, JaNote.C.intervalUpTo(JaNote.F)),
      () -> Assertions.assertEquals(6, JaNote.C.intervalUpTo(JaNote.F_SHARP)),
      () -> Assertions.assertEquals(7, JaNote.C.intervalUpTo(JaNote.G)),
      () -> Assertions.assertEquals(8, JaNote.C.intervalUpTo(JaNote.G_SHARP)),
      () -> Assertions.assertEquals(9, JaNote.C.intervalUpTo(JaNote.A)),
      () -> Assertions.assertEquals(10, JaNote.C.intervalUpTo(JaNote.A_SHARP)),
      () -> Assertions.assertEquals(11, JaNote.C.intervalUpTo(JaNote.B)));
  }

  @Test
  public void testNext()
  {
    Assertions.assertAll(
      () -> Assertions.assertEquals(JaNote.C_SHARP, JaNote.C.next()),
      () -> Assertions.assertEquals(JaNote.D, JaNote.C_SHARP.next()),
      () -> Assertions.assertEquals(JaNote.D_SHARP, JaNote.D.next()),
      () -> Assertions.assertEquals(JaNote.E, JaNote.D_SHARP.next()),
      () -> Assertions.assertEquals(JaNote.F, JaNote.E.next()),
      () -> Assertions.assertEquals(JaNote.F_SHARP, JaNote.F.next()),
      () -> Assertions.assertEquals(JaNote.G, JaNote.F_SHARP.next()),
      () -> Assertions.assertEquals(JaNote.G_SHARP, JaNote.G.next()),
      () -> Assertions.assertEquals(JaNote.A, JaNote.G_SHARP.next()),
      () -> Assertions.assertEquals(JaNote.A_SHARP, JaNote.A.next()),
      () -> Assertions.assertEquals(JaNote.B, JaNote.A_SHARP.next()),
      () -> Assertions.assertEquals(JaNote.C, JaNote.B.next())
    );
  }

  @Test
  public void testPrevious()
  {
    Assertions.assertAll(
      () -> Assertions.assertEquals(JaNote.C, JaNote.C_SHARP.previous()),
      () -> Assertions.assertEquals(JaNote.C_SHARP, JaNote.D.previous()),
      () -> Assertions.assertEquals(JaNote.D, JaNote.D_SHARP.previous()),
      () -> Assertions.assertEquals(JaNote.D_SHARP, JaNote.E.previous()),
      () -> Assertions.assertEquals(JaNote.E, JaNote.F.previous()),
      () -> Assertions.assertEquals(JaNote.F, JaNote.F_SHARP.previous()),
      () -> Assertions.assertEquals(JaNote.F_SHARP, JaNote.G.previous()),
      () -> Assertions.assertEquals(JaNote.G, JaNote.G_SHARP.previous()),
      () -> Assertions.assertEquals(JaNote.G_SHARP, JaNote.A.previous()),
      () -> Assertions.assertEquals(JaNote.A, JaNote.A_SHARP.previous()),
      () -> Assertions.assertEquals(JaNote.A_SHARP, JaNote.B.previous()),
      () -> Assertions.assertEquals(JaNote.B, JaNote.C.previous())
    );
  }

  @Test
  public void testStepByPositive()
  {
    Assertions.assertAll(
      () -> Assertions.assertEquals(JaNote.C, JaNote.C.stepBy(0)),
      () -> Assertions.assertEquals(JaNote.C_SHARP, JaNote.C.stepBy(1)),
      () -> Assertions.assertEquals(JaNote.D, JaNote.C.stepBy(2)),
      () -> Assertions.assertEquals(JaNote.D_SHARP, JaNote.C.stepBy(3)),
      () -> Assertions.assertEquals(JaNote.E, JaNote.C.stepBy(4)),
      () -> Assertions.assertEquals(JaNote.F, JaNote.C.stepBy(5)),
      () -> Assertions.assertEquals(JaNote.F_SHARP, JaNote.C.stepBy(6)),
      () -> Assertions.assertEquals(JaNote.G, JaNote.C.stepBy(7)),
      () -> Assertions.assertEquals(JaNote.G_SHARP, JaNote.C.stepBy(8)),
      () -> Assertions.assertEquals(JaNote.A, JaNote.C.stepBy(9)),
      () -> Assertions.assertEquals(JaNote.A_SHARP, JaNote.C.stepBy(10)),
      () -> Assertions.assertEquals(JaNote.B, JaNote.C.stepBy(11)),
      () -> Assertions.assertEquals(JaNote.C, JaNote.C.stepBy(12))
    );
  }

  @Test
  public void testStepByNegative()
  {
    Assertions.assertAll(
      () -> Assertions.assertEquals(JaNote.C, JaNote.C.stepBy(-12)),
      () -> Assertions.assertEquals(JaNote.C_SHARP, JaNote.C.stepBy(-11)),
      () -> Assertions.assertEquals(JaNote.D, JaNote.C.stepBy(-10)),
      () -> Assertions.assertEquals(JaNote.D_SHARP, JaNote.C.stepBy(-9)),
      () -> Assertions.assertEquals(JaNote.E, JaNote.C.stepBy(-8)),
      () -> Assertions.assertEquals(JaNote.F, JaNote.C.stepBy(-7)),
      () -> Assertions.assertEquals(JaNote.F_SHARP, JaNote.C.stepBy(-6)),
      () -> Assertions.assertEquals(JaNote.G, JaNote.C.stepBy(-5)),
      () -> Assertions.assertEquals(JaNote.G_SHARP, JaNote.C.stepBy(-4)),
      () -> Assertions.assertEquals(JaNote.A, JaNote.C.stepBy(-3)),
      () -> Assertions.assertEquals(JaNote.A_SHARP, JaNote.C.stepBy(-2)),
      () -> Assertions.assertEquals(JaNote.B, JaNote.C.stepBy(-1))
    );
  }
}
