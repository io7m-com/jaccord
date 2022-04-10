/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

/**
 * Documentation.
 */

module com.io7m.jaccord.documentation
{
  requires com.io7m.jaccord.chord_names.api;
  requires com.io7m.jaccord.chord_names.vanilla;
  requires com.io7m.jaccord.core;
  requires com.io7m.jaccord.cpdsl.midi;
  requires com.io7m.jaccord.cpdsl;
  requires com.io7m.jaccord.parser.api;
  requires com.io7m.jaccord.parser.vanilla;
  requires com.io7m.jaccord.scales.api;
  requires com.io7m.jaccord.scales.spi;
  requires com.io7m.jaccord.scales.vanilla;

  exports com.io7m.jaccord.documentation;
}