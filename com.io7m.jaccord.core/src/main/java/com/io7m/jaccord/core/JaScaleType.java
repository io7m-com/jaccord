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

import com.io7m.immutables.styles.ImmutablesStyleType;
import io.vavr.collection.Set;
import io.vavr.collection.Vector;
import org.immutables.value.Value;

import static org.immutables.value.Value.Immutable;

/**
 * The type of scales.
 */

@ImmutablesStyleType
@Immutable
public interface JaScaleType
{
  /**
   * @return The root note of the scale
   */

  @Value.Parameter
  JaNote root();

  /**
   * @return The intervals of the scale relative to the root note
   */

  @Value.Parameter
  JaScaleIntervals intervals();

  /**
   * @return The notes of the scale in order
   */

  @Value.Derived
  default Vector<JaNote> notesOrdered()
  {
    Vector<JaNote> notes = Vector.of(this.root());
    for (final Integer i : this.intervals().intervals()) {
      notes = notes.append(this.root().stepBy(i.intValue()));
    }
    return notes;
  }

  /**
   * @return The notes of the scale
   */

  @Value.Derived
  default Set<JaNote> notes()
  {
    return this.intervals()
      .intervals()
      .map(i -> this.root().stepBy(i.intValue()))
      .add(this.root());
  }
}
