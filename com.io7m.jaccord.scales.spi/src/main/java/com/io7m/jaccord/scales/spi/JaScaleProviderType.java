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

package com.io7m.jaccord.scales.spi;

import com.io7m.jaccord.core.JaScaleIntervals;
import com.io7m.jaccord.core.JaScaleNamed;
import io.vavr.collection.List;
import io.vavr.collection.SortedSet;

import java.util.Optional;

/**
 * The type of scale providers.
 */

public interface JaScaleProviderType
{
  /**
   * @return The available scales
   */

  SortedSet<String> scales();

  /**
   * Retrieve the scale with the matching ID. The ID should be one of the values
   * returned by {@link #scales()}.
   *
   * @param id The scale ID
   *
   * @return A scale, if a scale with the given id exists
   */

  Optional<JaScaleNamed> scaleByID(
    String id);

  /**
   * Determine if there are any scales containing exactly the given intervals.
   *
   * @param intervals The scale intervals
   *
   * @return A list of scales matching the given intervals
   */

  List<JaScaleNamed> scalesByIntervals(
    JaScaleIntervals intervals);
}
