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

package com.io7m.jaccord.parser.vanilla;

import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.parser.api.JaAccidentalEncoding;
import com.io7m.jaccord.parser.api.JaParseError;
import com.io7m.jaccord.parser.api.JaParserConfigurationType;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import io.vavr.control.Validation;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class JaNoteParsing
{
  private static final Pattern NOTE_ASCII =
    Pattern.compile("([ABCDEFG])([#b])?");
  private static final Pattern NOTE_UNICODE =
    Pattern.compile("([ABCDEFG])([♯♭])?");
  private static final Pattern NOTE_UNICODE_AND_ASCII =
    Pattern.compile("([ABCDEFG])([♯♭#b])?");

  private JaNoteParsing()
  {
    throw new UnreachableCodeException();
  }

  static Validation<Seq<JaParseError>, JaNote> parseNote(
    final JaParserConfigurationType config,
    final LexicalPosition<Path> position,
    final String text)
  {
    NullCheck.notNull(config, "Config");
    NullCheck.notNull(text, "Text");

    final Matcher matcher = createMatcher(config.accidentals(), text);
    if (!matcher.matches()) {
      return Validation.invalid(
        Vector.of(
          JaParseError.of(
            position,
            new StringBuilder(64)
              .append("Could not parse note.")
              .append(System.lineSeparator())
              .append("  Expected: A note of the form: ")
              .append(matcher.pattern())
              .append(System.lineSeparator())
              .append("  Received: ")
              .append(text)
              .append(System.lineSeparator())
              .toString(),
            Optional.empty())));
    }

    return Validation.valid(parseNoteActual(matcher));
  }

  private static JaNote parseNoteActual(
    final Matcher matcher)
  {
    final String name = matcher.group(1);
    final String accidental = matcher.group(2);

    switch (name) {
      case "A": {
        return applyAccidental(JaNote.A, accidental);
      }
      case "B": {
        return applyAccidental(JaNote.B, accidental);
      }
      case "C": {
        return applyAccidental(JaNote.C, accidental);
      }
      case "D": {
        return applyAccidental(JaNote.D, accidental);
      }
      case "E": {
        return applyAccidental(JaNote.E, accidental);
      }
      case "F": {
        return applyAccidental(JaNote.F, accidental);
      }
      case "G": {
        return applyAccidental(JaNote.G, accidental);
      }
      default:
        throw new UnreachableCodeException();
    }
  }

  private static JaNote applyAccidental(
    final JaNote note,
    final String accidental)
  {
    if (accidental == null) {
      return note;
    }

    switch (accidental) {
      case "#":
      case "♯":
        return note.next();
      case "b":
      case "♭":
        return note.previous();
      default:
        throw new UnreachableCodeException();
    }
  }

  private static Matcher createMatcher(
    final JaAccidentalEncoding accidentals,
    final String text)
  {
    switch (accidentals) {
      case UNICODE_ACCIDENTALS: {
        return NOTE_UNICODE.matcher(text);
      }
      case ASCII_ACCIDENTALS: {
        return NOTE_ASCII.matcher(text);
      }
      case UNICODE_AND_ASCII_ACCIDENTALS: {
        return NOTE_UNICODE_AND_ASCII.matcher(text);
      }
    }
    throw new UnreachableCodeException();
  }
}
