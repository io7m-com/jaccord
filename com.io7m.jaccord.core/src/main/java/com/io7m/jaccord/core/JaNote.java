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

package com.io7m.jaccord.core;

import com.io7m.junreachable.UnreachableCodeException;

import java.util.Objects;

/**
 * A note, otherwise known as a <i>pitch class</i>.
 */

public enum JaNote
{
  /**
   * C
   */

  C("C"),

  /**
   * C#
   */

  C_SHARP("C♯"),

  /**
   * D
   */

  D("D"),

  /**
   * D#
   */

  D_SHARP("D♯"),

  /**
   * E
   */

  E("E"),

  /**
   * F
   */

  F("F"),

  /**
   * F#
   */

  F_SHARP("F♯"),

  /**
   * G
   */

  G("G"),

  /**
   * G#
   */

  G_SHARP("G♯"),

  /**
   * A
   */

  A("A"),

  /**
   * A#
   */

  A_SHARP("A♯"),

  /**
   * B
   */

  B("B");

  private final String note_name;

  JaNote(
    final String in_note_name)
  {
    this.note_name = Objects.requireNonNull(in_note_name, "Note name");
  }

  /**
   * @return The name of the note
   */

  public String noteName()
  {
    return this.note_name;
  }

  /**
   * Determine the number of semitones to the given note from the current note.
   *
   * @param target_note The target note
   *
   * @return The number of semitones by which the current note would need to be
   * increased to reach the target note
   */

  public int intervalUpTo(
    final JaNote target_note)
  {
    JaNote note_current = this;
    int semitones = 0;

    while (true) {
      if (note_current == target_note) {
        return semitones;
      }
      note_current = note_current.next();
      ++semitones;
    }
  }

  /**
   * Increase the current note by a given number of semitones.
   *
   * @param semitones The semitones by which to increase a note
   *
   * @return The resulting note
   */

  public JaNote stepBy(
    final int semitones)
  {
    if (semitones >= 0) {
      JaNote curr = this;
      for (int index = 0; index < semitones; ++index) {
        curr = curr.next();
      }
      return curr;
    }

    JaNote curr = this;
    for (int index = 0; index < Math.abs(semitones); ++index) {
      curr = curr.previous();
    }
    return curr;
  }

  /**
   * Determine the previous pitch class by increasing the current note by a
   * semitone.
   *
   * @return The previous pitch class
   */

  public JaNote previous()
  {
    switch (this) {
      case C:
        return B;
      case C_SHARP:
        return C;
      case D:
        return C_SHARP;
      case D_SHARP:
        return D;
      case E:
        return D_SHARP;
      case F:
        return E;
      case F_SHARP:
        return F;
      case G:
        return F_SHARP;
      case G_SHARP:
        return G;
      case A:
        return G_SHARP;
      case A_SHARP:
        return A;
      case B:
        return A_SHARP;
    }

    throw new UnreachableCodeException();
  }

  /**
   * Determine the next pitch class by increasing the current note by a
   * semitone.
   *
   * @return The next pitch class
   */

  public JaNote next()
  {
    switch (this) {
      case C:
        return C_SHARP;
      case C_SHARP:
        return D;
      case D:
        return D_SHARP;
      case D_SHARP:
        return E;
      case E:
        return F;
      case F:
        return F_SHARP;
      case F_SHARP:
        return G;
      case G:
        return G_SHARP;
      case G_SHARP:
        return A;
      case A:
        return A_SHARP;
      case A_SHARP:
        return B;
      case B:
        return C;
    }

    throw new UnreachableCodeException();
  }
}
