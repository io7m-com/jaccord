/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import com.io7m.jaccord.chord_names.vanilla.JaChordNames;
import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordIntervals;
import com.io7m.jaccord.core.JaNote;
import org.jaudiolibs.jnajack.Jack;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackMidi;
import org.jaudiolibs.jnajack.JackOptions;
import org.jaudiolibs.jnajack.JackPortConnectCallback;
import org.jaudiolibs.jnajack.JackPortFlags;
import org.jaudiolibs.jnajack.JackPortType;
import org.jaudiolibs.jnajack.JackStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.ShortMessage;
import java.util.EnumSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class JaMIDIChordAnalyzer
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JaMIDIChordAnalyzer.class);

  private JaMIDIChordAnalyzer()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final var jack = Jack.getInstance();

    final var client =
      jack.openClient(
        "jaMidiChord",
        EnumSet.of(JackOptions.JackNoStartServer),
        EnumSet.noneOf(JackStatus.class)
      );

    try {
      final var port =
        client.registerPort(
          "midi",
          JackPortType.MIDI,
          EnumSet.of(JackPortFlags.JackPortIsInput)
        );

      client.setPortConnectCallback(
        new JackPortConnectCallback()
        {
          @Override
          public void portsConnected(
            final JackClient client,
            final String portName1,
            final String portName2)
          {
            LOG.debug("port connected: {} -> {}", portName1, portName2);
          }

          @Override
          public void portsDisconnected(
            final JackClient client,
            final String portName1,
            final String portName2)
          {
            LOG.debug("port disconnected: {} -> {}", portName1, portName2);
          }
        });

      final var workingNotes =
        new TreeSet<Integer>();

      final var chordChanges =
        new LinkedBlockingQueue<SortedSet<Integer>>(10_000);

      final var event = new JackMidi.Event();
      client.setProcessCallback((peer, nframes) -> {
        try {
          final var eventCount = JackMidi.getEventCount(port);
          var changed = false;

          for (int index = 0; index < eventCount; ++index) {
            JackMidi.eventGet(event, port, index);
            final var buffer = new byte[event.size()];
            event.read(buffer);
            if (buffer.length == 3) {
              final var message =
                new ShortMessage(buffer[0], buffer[1], buffer[2]);

              switch (message.getCommand()) {
                case ShortMessage.NOTE_ON: {
                  workingNotes.add(Integer.valueOf(message.getData1()));
                  changed = true;
                  break;
                }
                case ShortMessage.NOTE_OFF: {
                  workingNotes.remove(Integer.valueOf(message.getData1()));
                  changed = true;
                  break;
                }
                default: {
                  break;
                }
              }
            }
          }

          if (changed) {
            if (!workingNotes.isEmpty()) {
              chordChanges.add(new TreeSet<>(workingNotes));
            }
          }
          return true;
        } catch (final Exception e) {
          LOG.error("error: ", e);
          return false;
        }
      });

      client.activate();

      while (true) {
        final var notes = chordChanges.poll(1L, TimeUnit.SECONDS);
        if (notes == null) {
          continue;
        }

        processChordChange(notes);
      }
    } finally {
      client.close();
    }
  }

  private static void processChordChange(
    final SortedSet<Integer> notes)
  {
    LOG.debug("chordChange: {}", notes);

    if (notes.size() < 3) {
      return;
    }

    final var first =
      notes.first();
    final var root =
      noteNumberToNote(first.intValue());

    final SortedSet<Integer> rest =
      notes.tailSet(Integer.valueOf(first.intValue() + 1))
        .stream()
        .map(x -> Integer.valueOf(x.intValue() - first.intValue()))
        .map(x -> Integer.valueOf(x.intValue() % 24))
        .collect(Collectors.toCollection(TreeSet::new));

    final var chord =
      JaChord.builder()
        .setRoot(root)
        .setIntervals(JaChordIntervals.of(io.vavr.collection.TreeSet.ofAll(rest)))
        .build();

    JaChordDemo.forChord(chord);
  }

  private static JaNote noteNumberToNote(
    final int note)
  {
    final var noteMod = note % 12;
    switch (noteMod) {
      case 0:
        return JaNote.C;
      case 1:
        return JaNote.C_SHARP;
      case 2:
        return JaNote.D;
      case 3:
        return JaNote.D_SHARP;
      case 4:
        return JaNote.E;
      case 5:
        return JaNote.F;
      case 6:
        return JaNote.F_SHARP;
      case 7:
        return JaNote.G;
      case 8:
        return JaNote.G_SHARP;
      case 9:
        return JaNote.A;
      case 10:
        return JaNote.A_SHARP;
      case 11:
        return JaNote.B;
      default:
        throw new IllegalStateException(
          String.format("Unexpected value: %d", Integer.valueOf(noteMod))
        );
    }
  }
}
