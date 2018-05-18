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

package com.io7m.jaccord.scales.api;

import com.io7m.jaccord.core.JaScaleIntervals;
import com.io7m.jaccord.core.JaScaleNamed;
import com.io7m.jaccord.scales.spi.JaScaleProviderType;
import java.util.Objects;
import io.vavr.collection.List;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * A {@link ServiceLoader} interface to scale providers.
 */

public final class JaScales
{
  private JaScales()
  {

  }

  /**
   * @return The available scales
   */

  public static SortedSet<String> scales()
  {
    final ServiceLoader<JaScaleProviderType> loader =
      ServiceLoader.load(JaScaleProviderType.class);

    TreeSet<String> names = TreeSet.empty();
    final Iterator<JaScaleProviderType> iter = loader.iterator();
    while (iter.hasNext()) {
      final JaScaleProviderType prov = iter.next();
      names = names.addAll(prov.scales());
    }
    return names;
  }

  /**
   * Retrieve any scales with the matching ID. The ID should be one of the
   * values returned by {@link #scales()}.
   *
   * @param id The scale ID
   *
   * @return A list of matching scales
   */

  public static List<JaScaleNamed> scalesByID(
    final String id)
  {
    Objects.requireNonNull(id, "ID");

    final ServiceLoader<JaScaleProviderType> loader =
      ServiceLoader.load(JaScaleProviderType.class);

    List<JaScaleNamed> scales = List.empty();
    final Iterator<JaScaleProviderType> iter = loader.iterator();
    while (iter.hasNext()) {
      final JaScaleProviderType prov = iter.next();
      final List<JaScaleNamed> f_scales = scales;
      scales = prov.scaleByID(id).map(f_scales::append).orElse(scales);
    }
    return scales;
  }

  /**
   * Determine if there are any scales containing exactly the given intervals.
   *
   * @param intervals The scale intervals
   *
   * @return A list of scales matching the given intervals
   */

  public static List<JaScaleNamed> scalesByIntervals(
    final JaScaleIntervals intervals)
  {
    Objects.requireNonNull(intervals, "Intervals");

    final ServiceLoader<JaScaleProviderType> loader =
      ServiceLoader.load(JaScaleProviderType.class);

    List<JaScaleNamed> scales = List.empty();
    final Iterator<JaScaleProviderType> iter = loader.iterator();
    while (iter.hasNext()) {
      final JaScaleProviderType prov = iter.next();
      scales = scales.appendAll(prov.scalesByIntervals(intervals));
    }
    return scales;
  }
}
