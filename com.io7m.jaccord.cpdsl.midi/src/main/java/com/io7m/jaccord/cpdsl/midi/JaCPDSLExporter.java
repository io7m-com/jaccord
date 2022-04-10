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

package com.io7m.jaccord.cpdsl.midi;

import com.io7m.jaccord.cpdsl.JaCPDSL;
import com.io7m.jaccord.cpdsl.midi.internal.JaMidiChord;
import com.io7m.jaccord.cpdsl.midi.internal.JaMidiChords;
import com.io7m.jaccord.cpdsl.midi.internal.JaVoiceLeading;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Functions for exporting progressions to MIDI.
 */

public final class JaCPDSLExporter
{
  private static final JaCPDSLExporterConfiguration DEFAULT_CONFIGURATION =
    JaCPDSLExporterConfiguration.builder()
      .setDoubleRoot(false)
      .build();

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
   * @param configuration The exporter configuration
   * @param progression   The input progression
   *
   * @return A MIDI sequence
   *
   * @throws InvalidMidiDataException If MIDI data could not be produced
   */

  public static Sequence exportWithConfiguration(
    final JaCPDSLExporterConfiguration configuration,
    final JaCPDSL.Progression progression)
    throws InvalidMidiDataException
  {
    Objects.requireNonNull(configuration, "Configuration");
    Objects.requireNonNull(progression, "Progression");

    final var sequence =
      new Sequence(Sequence.SMPTE_30, 30);
    final var track = sequence.createTrack();

    //
    // Set tempo.
    //

    {
      final var w = microSecondsForQuarterNoteAtTempo(120);
      final var mt = new MetaMessage();
      final byte[] bt = {
        (byte) ((w >>> 16) & 0xffL),
        (byte) ((w >>> 8) & 0xffL),
        (byte) (w & 0xffL),
      };
      mt.setMessage(0x51, bt, 3);
      final var me = new MidiEvent(mt, 0L);
      track.add(me);
    }

    //
    // Set time signature.
    //

    {
      final var mt = new MetaMessage();
      final byte[] bt = {
        (byte) 0x4,
        (byte) 0x2,
        (byte) 24,
        (byte) 8,
      };
      mt.setMessage(0x58, bt, 4);
      final var me = new MidiEvent(mt, 0L);
      track.add(me);
    }

    //
    // Set track name.
    //

    {
      final var mt = new MetaMessage();
      final var name = "Piano";
      mt.setMessage(
        0x03,
        name.getBytes(StandardCharsets.US_ASCII),
        name.length());
      final var me = new MidiEvent(mt, 0L);
      track.add(me);
    }

    //
    // Enable omni.
    //

    {
      final var mm = new ShortMessage();
      mm.setMessage(0xB0, 0x7D, 0x00);
      final var me = new MidiEvent(mm, 0L);
      track.add(me);
    }

    //
    // Enable polyphony.
    //

    {
      final var mm = new ShortMessage();
      mm.setMessage(0xB0, 0x7F, 0x00);
      final var me = new MidiEvent(mm, 0L);
      track.add(me);
    }

    //
    // Set instrument to piano.
    //

    {
      final var mm = new ShortMessage();
      mm.setMessage(0xC0, 0x00, 0x00);
      final var me = new MidiEvent(mm, 0L);
      track.add(me);
    }

    exportProgression(configuration, track, progression);
    return sequence;
  }

  /**
   * Produce a MIDI sequence from the given progression. The chords are produced
   * as simple root-position voicings with no attempt made to make the result
   * more musically pleasing.
   *
   * @param progression The input progression
   *
   * @return A MIDI sequence
   *
   * @throws InvalidMidiDataException If MIDI data could not be produced
   */

  public static Sequence export(
    final JaCPDSL.Progression progression)
    throws InvalidMidiDataException
  {
    return exportWithConfiguration(DEFAULT_CONFIGURATION, progression);
  }

  private static void exportProgression(
    final JaCPDSLExporterConfiguration configuration,
    final Track track,
    final JaCPDSL.Progression progression)
    throws InvalidMidiDataException
  {
    var time = 0L;
    final var whole = 1800L;
    final var quarter = whole / 4L;

    final var changes = progression.changes();
    List<JaMidiChord> chords = new ArrayList<JaMidiChord>(changes.size());

    for (final var ch : changes) {
      final var duration = (long) ch.beats() * quarter;
      final var timeNext = time + duration;

      final var midiChord =
        JaMidiChords.midiChordOf(
          configuration,
          time,
          timeNext,
          ch.chord().evaluate());

      chords.add(midiChord);
      time = timeNext;
    }

    if (configuration.voiceLeading()) {
      chords = JaVoiceLeading.withVoiceLeading(chords);
    }

    for (final var midiChord : chords) {
      addChord(track, midiChord);
    }

    //
    // Set end of track.
    //

    final var mt = new MetaMessage();
    final byte[] bet = {};
    mt.setMessage(0x2F, bet, 0);
    final var me = new MidiEvent(mt, time);
    track.add(me);
  }

  private static void addChord(
    final Track track,
    final JaMidiChord midiChord)
    throws InvalidMidiDataException
  {
    for (final var note : midiChord.midiNotes()) {
      addNote(
        track,
        midiChord.timeStart(),
        midiChord.timeEnd(),
        note.intValue()
      );
    }
  }

  private static void addNote(
    final Track track,
    final long time,
    final long time_end,
    final int note)
    throws InvalidMidiDataException
  {
    //
    // Note on.
    //

    {
      final var mm = new ShortMessage();
      mm.setMessage(0x90, note, 0x7f);
      final var me = new MidiEvent(mm, time);
      track.add(me);
    }

    //
    // Note off.
    //

    {
      final var mm = new ShortMessage();
      mm.setMessage(0x80, note, 0x00);
      final var me = new MidiEvent(mm, time_end);
      track.add(me);
    }
  }
}
