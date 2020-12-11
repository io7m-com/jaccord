/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.jaccord.cpdsl.midi.internal;

import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaIntervals;
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.cpdsl.midi.JaCPDSLExporterConfiguration;
import com.io7m.junreachable.UnreachableCodeException;

import java.util.ArrayList;

public final class JaMidiChords
{
  private JaMidiChords()
  {

  }

  public static JaMidiChord midiChordOf(
    final JaCPDSLExporterConfiguration configuration,
    final long timeStart,
    final long timeEnd,
    final JaChord chord)
  {
    final int root = toMidiNote(chord.root());

    final var newNotes = new ArrayList<Integer>();
    newNotes.add(Integer.valueOf(root));

    if (configuration.doubleRoot()) {
      newNotes.add(Integer.valueOf(root - 12));
    }

    for (final Integer i : chord.intervals().intervalsNormalized()) {
      if (i.intValue() == JaIntervals.FIFTH.intValue()
        || i.intValue() == JaIntervals.TRITAVE.intValue()) {
        if (configuration.omitFifth()) {
          continue;
        }
      }

      newNotes.add(Integer.valueOf(root + i.intValue()));
    }

    return JaMidiChord.builder()
      .setTimeStart(timeStart)
      .setTimeEnd(timeEnd)
      .setMidiNotes(newNotes)
      .build();
  }

  private static int toMidiNote(
    final JaNote root)
  {
    switch (root) {
      case C:
        return 60;
      case C_SHARP:
        return 61;
      case D:
        return 62;
      case D_SHARP:
        return 63;
      case E:
        return 64;
      case F:
        return 65;
      case F_SHARP:
        return 66;
      case G:
        return 67;
      case G_SHARP:
        return 68;
      case A:
        return 69;
      case A_SHARP:
        return 70;
      case B:
        return 71;
    }

    throw new UnreachableCodeException();
  }
}
