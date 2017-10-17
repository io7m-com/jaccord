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

package com.io7m.jaccord.tests.core.examples;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class JaMIDIDemo
{
  private JaMIDIDemo()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final Sequence s = new Sequence(Sequence.SMPTE_30, 30);
    final Track t = s.createTrack();

    /*
     * Set tempo.
     */

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
      t.add(me);
    }

    /*
     * Set time signature.
     */

    {
      final MetaMessage mt = new MetaMessage();
      final byte[] bt = {
        (byte) 0x4,
        (byte) 0x2,
        (byte) 24,
        (byte) 8
      };
      mt.setMessage(0x58, bt, 4);
      final MidiEvent me = new MidiEvent(mt, 0L);
      t.add(me);
    }

    /*
     * Set track name.
     */

    {
      final MetaMessage mt = new MetaMessage();
      final String name = "Piano";
      mt.setMessage(
        0x03,
        name.getBytes(StandardCharsets.US_ASCII),
        name.length());
      final MidiEvent me = new MidiEvent(mt, 0L);
      t.add(me);
    }

    /*
     * Enable omni.
     */

    {
      final ShortMessage mm = new ShortMessage();
      mm.setMessage(0xB0, 0x7D, 0x00);
      final MidiEvent me = new MidiEvent(mm, 0L);
      t.add(me);
    }

    /*
     * Enable polyphony.
     */

    {
      final ShortMessage mm = new ShortMessage();
      mm.setMessage(0xB0, 0x7F, 0x00);
      final MidiEvent me = new MidiEvent(mm, 0L);
      t.add(me);
    }

    /*
     * Set instrument to piano.
     */

    {
      final ShortMessage mm = new ShortMessage();
      mm.setMessage(0xC0, 0x00, 0x00);
      final MidiEvent me = new MidiEvent(mm, 0L);
      t.add(me);
    }

    /*
     * Note on.
     */

    {
      final ShortMessage mm = new ShortMessage();
      mm.setMessage(0x90, 0x3C, 0x7f);
      final MidiEvent me = new MidiEvent(mm, 0L);
      t.add(me);
    }

    /*
     * Note off.
     */

    {
      final ShortMessage mm = new ShortMessage();
      mm.setMessage(0x80, 0x3C, 0x00);
      final MidiEvent me = new MidiEvent(mm, 1800L);
      t.add(me);
    }

    /*
     * Set end of track.
     */

    {
      final MetaMessage mt = new MetaMessage();
      final byte[] bet = {}; // empty array
      mt.setMessage(0x2F, bet, 0);
      final MidiEvent me = new MidiEvent(mt, 1801L);
      t.add(me);
    }

    try (final OutputStream out = Files.newOutputStream(Paths.get("output.mid"))) {
      MidiSystem.write(s, 1, out);
    }
  }

  private static long microSecondsForQuarterNoteAtTempo(
    final int bpm)
  {
    return 60_000_000L / (long) bpm;
  }
}
