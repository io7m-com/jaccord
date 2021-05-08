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

import org.apache.commons.collections4.iterators.PushbackIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Brute force voice leading.
 */

public final class JaVoiceLeading
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JaVoiceLeading.class);

  private JaVoiceLeading()
  {

  }

  /**
   * Try to transform a list of chords into a form with better voice leading.
   *
   * @param chords The input chords
   *
   * @return The result chords
   */

  public static List<JaMidiChord> withVoiceLeading(
    final List<JaMidiChord> chords)
  {
    final var queue = new ArrayList<>(chords);
    final var results = new ArrayList<JaMidiChord>();

    final var iter = new PushbackIterator<>(queue.iterator());
    results.add(chords.get(0));

    while (iter.hasNext()) {
      final var chord0 = iter.next();
      if (iter.hasNext()) {
        final var chord1 =
          iter.next();
        final var chord1i =
          findBestInversionOf(chord0, chord1);

        results.add(chord1i);
        iter.pushback(chord1i);
      }
    }

    return List.copyOf(results);
  }

  private static List<JaMidiChord> inversionsOf(
    final JaMidiChord chord)
  {
    final var results = new ArrayList<JaMidiChord>();

    final var notes = new ArrayList<>(chord.midiNotes());
    final var noteCount = notes.size();
    for (int iteration = 0; iteration < noteCount; ++iteration) {
      final var newNotes = new ArrayList<>(notes);
      for (int note = 0; note < iteration; ++note) {
        newNotes.set(note, Integer.valueOf(newNotes.get(note).intValue() + 12));
      }
      Collections.sort(newNotes);
      results.add(chord.withMidiNotes(newNotes));
    }

    for (int iteration = 0; iteration < noteCount; ++iteration) {
      final var newNotes = new ArrayList<>(notes);
      for (int note = 0; note < iteration; ++note) {
        final int nIndex = (noteCount - 1) - note;
        newNotes.set(
          nIndex,
          Integer.valueOf(newNotes.get(nIndex).intValue() - 12));
      }
      Collections.sort(newNotes);
      results.add(chord.withMidiNotes(newNotes));
    }

    return List.copyOf(results);
  }

  private static JaMidiChord findBestInversionOf(
    final JaMidiChord chord0,
    final JaMidiChord chord1)
  {
    final var inversions = inversionsOf(chord1);
    var bestSoFar = 0;
    var smallest = Integer.MAX_VALUE;

    for (int index = 0; index < inversions.size(); ++index) {
      final int diff = differenceBetween(chord0, inversions.get(index));
      if (diff < smallest) {
        bestSoFar = index;
        smallest = diff;
      }
    }

    return inversions.get(bestSoFar);
  }

  private static int differenceBetween(
    final JaMidiChord chord0,
    final JaMidiChord chord1)
  {
    final var c0Notes = chord0.midiNotes();
    final var c1Notes = chord1.midiNotes();

    final var lowest0 = c0Notes.get(0);
    final var lowest1 = c1Notes.get(0);
    final var highest0 = c0Notes.get(c0Notes.size() - 1);
    final var highest1 = c1Notes.get(c1Notes.size() - 1);

    final var lowDiff =
      Math.abs(lowest0.intValue() - lowest1.intValue());
    final var hiDiff =
      Math.abs(highest0.intValue() - highest1.intValue());

    return lowDiff + hiDiff;
  }
}
