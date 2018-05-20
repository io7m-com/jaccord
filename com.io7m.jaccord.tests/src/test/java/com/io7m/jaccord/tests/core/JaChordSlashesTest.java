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

import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordIntervals;
import com.io7m.jaccord.core.JaChordSlash;
import com.io7m.jaccord.core.JaChordSlashes;
import com.io7m.jaccord.core.JaExceptionChord;
import com.io7m.jaccord.core.JaNote;
import io.vavr.collection.TreeSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public final class JaChordSlashesTest
{
  @Test
  public void testTooFewNotes()
    throws JaExceptionChord
  {
    final JaChord chord =
      JaChord.of(JaNote.C, JaChordIntervals.of(TreeSet.of(Integer.valueOf(3))));

    final Optional<JaChordSlash> slash_opt =
      JaChordSlashes.slash(chord);

    Assertions.assertFalse(slash_opt.isPresent());
  }

  @Test
  public void testSlashMajor7()
    throws JaExceptionChord
  {
    final JaChord chord =
      JaChord.of(
        JaNote.C,
        JaChordIntervals.of(TreeSet.of(
          Integer.valueOf(4),
          Integer.valueOf(7),
          Integer.valueOf(11))));

    final Optional<JaChordSlash> slash_opt =
      JaChordSlashes.slash(chord);

    Assertions.assertTrue(slash_opt.isPresent());

    final JaChordSlash slash = slash_opt.get();
    Assertions.assertEquals(JaNote.C, slash.bass());
    Assertions.assertEquals(JaNote.E, slash.chord().root());
    Assertions.assertEquals(
      JaChordIntervals.of(TreeSet.of(
        Integer.valueOf(3),
        Integer.valueOf(7))),
      slash.chord().intervals());
  }
}
