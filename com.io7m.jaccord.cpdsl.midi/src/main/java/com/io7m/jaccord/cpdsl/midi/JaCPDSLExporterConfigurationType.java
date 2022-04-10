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

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import static org.immutables.value.Value.Immutable;

/**
 * Configuration values for MIDI exporters.
 */

@ImmutablesStyleType
@Immutable
public interface JaCPDSLExporterConfigurationType
{
  /**
   * @return {@code true} if the root note of each chord should be doubled
   */

  @Value.Default
  default boolean doubleRoot()
  {
    return false;
  }

  /**
   * @return {@code true} if the fifths of each chord should be omitted
   */

  @Value.Default
  default boolean omitFifth()
  {
    return false;
  }

  /**
   * @return {@code true} if the notes of the chords should be adjusted for voice leading
   */

  @Value.Default
  default boolean voiceLeading()
  {
    return false;
  }
}
