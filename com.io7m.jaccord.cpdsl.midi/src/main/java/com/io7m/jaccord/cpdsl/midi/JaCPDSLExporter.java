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

package com.io7m.jaccord.cpdsl.midi;

import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.cpdsl.JaCPDSL;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.nio.charset.StandardCharsets;

/**
 * Functions for exporting progressions to MIDI.
 */

public final class JaCPDSLExporter
{
  private JaCPDSLExporter()
  {

  }

  private static long microSecondsForQuarterNoteAtTempo(
    final int bpm)
  {
    return 60_000_000L / (long) bpm;
  }

  /**
   * Produce a MIDI sequence from the given progression. The chords are produced
   * as simple root-position voicings with no attempt made to make the result
   * more musically pleasing.
   *
   * @param progression The input progression
   *
   * @return A MIDI sequence
   */

  public static Sequence export(
    final JaCPDSL.Progression progression)
  {
    NullCheck.notNull(progression, "Progression");

    try {
      final Sequence sequence =
        new Sequence(Sequence.SMPTE_30, 30);
      final Track track = sequence.createTrack();

      //
      // Set tempo.
      //

      {
        final long w = microSecondsForQuarterNoteAtTempo(120);
        System.out.println("Tempo: " + w);

        final MetaMessage mt = new MetaMessage();
        final byte[] bt = {
          (byte) ((w >>> 16) & 0xffL),
          (byte) ((w >>> 8) & 0xffL),
          (byte) (w & 0xffL),
        };
        mt.setMessage(0x51, bt, 3);
        final MidiEvent me = new MidiEvent(mt, 0L);
        track.add(me);
      }

      //
      // Set time signature.
      //

      {
        final MetaMessage mt = new MetaMessage();
        final byte[] bt = {
          (byte) 0x4,
          (byte) 0x2,
          (byte) 24,
          (byte) 8,
        };
        mt.setMessage(0x58, bt, 4);
        final MidiEvent me = new MidiEvent(mt, 0L);
        track.add(me);
      }

      //
      // Set track name.
      //

      {
        final MetaMessage mt = new MetaMessage();
        final String name = "Piano";
        mt.setMessage(
          0x03,
          name.getBytes(StandardCharsets.US_ASCII),
          name.length());
        final MidiEvent me = new MidiEvent(mt, 0L);
        track.add(me);
      }

      //
      // Enable omni.
      //

      {
        final ShortMessage mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7D, 0x00);
        final MidiEvent me = new MidiEvent(mm, 0L);
        track.add(me);
      }

      //
      // Enable polyphony.
      //

      {
        final ShortMessage mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7F, 0x00);
        final MidiEvent me = new MidiEvent(mm, 0L);
        track.add(me);
      }

      //
      // Set instrument to piano.
      //

      {
        final ShortMessage mm = new ShortMessage();
        mm.setMessage(0xC0, 0x00, 0x00);
        final MidiEvent me = new MidiEvent(mm, 0L);
        track.add(me);
      }

      exportProgression(track, progression);
      return sequence;
    } catch (final InvalidMidiDataException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void exportProgression(
    final Track track,
    final JaCPDSL.Progression progression)
  {
    long time = 0L;
    final long whole = 1800L;
    final long quarter = whole / 4L;

    for (final JaCPDSL.Change ch : progression.changes()) {
      final long duration = (long) ch.beats() * quarter;
      final long time_next = time + duration;
      addChord(track, time, time_next, ch.chord().evaluate());
      time = time_next;
    }

    //
    // Set end of track.
    //

    try {
      final MetaMessage mt = new MetaMessage();
      final byte[] bet = {};
      mt.setMessage(0x2F, bet, 0);
      final MidiEvent me = new MidiEvent(mt, time);
      track.add(me);
    } catch (final InvalidMidiDataException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private static void addChord(
    final Track track,
    final long time,
    final long time_end,
    final JaChord chord)
  {
    final int root = toMidiNote(chord.root());

    addNote(track, time, time_end, root);

    for (final Integer i : chord.intervals().intervalsNormalized()) {
      addNote(track, time, time_end, root + i.intValue());
    }
  }

  private static void addNote(
    final Track track,
    final long time,
    final long time_end,
    final int note)
  {
    try {
      //
      // Note on.
      //

      {
        final ShortMessage mm = new ShortMessage();
        mm.setMessage(0x90, note, 0x7f);
        final MidiEvent me = new MidiEvent(mm, time);
        track.add(me);
      }

      //
      // Note off.
      //

      {
        final ShortMessage mm = new ShortMessage();
        mm.setMessage(0x80, note, 0x00);
        final MidiEvent me = new MidiEvent(mm, time_end);
        track.add(me);
      }

    } catch (final InvalidMidiDataException e) {
      throw new UnreachableCodeException(e);
    }
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
