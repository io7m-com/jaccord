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

package com.io7m.jaccord.core;

/**
 * The chord types that will be returned by harmonization.
 */

public enum JaScaleHarmonizationChordTypes
{
  /**
   * Return {@code sus2} chords.
   */

  SUSPENDED_2_CHORDS,

  /**
   * Return {@code sus4} chords.
   */

  SUSPENDED_4_CHORDS,

  /**
   * Return triads.
   */

  TRIADS,

  /**
   * Return sixth chords.
   */

  SIXTH_CHORDS,

  /**
   * Return seventh chords.
   */

  SEVENTH_CHORDS,

  /**
   * Return ninth chords.
   */

  NINTH_CHORDS,

  /**
   * Return eleventh chords.
   */

  ELEVENTH_CHORDS,

  /**
   * Return thirteenth chords.
   */

  THIRTEENTH_CHORDS
}
