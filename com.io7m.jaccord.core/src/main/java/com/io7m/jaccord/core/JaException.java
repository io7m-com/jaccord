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

import java.util.Objects;

/**
 * The type of exceptions raised by the package.
 */

public abstract class JaException extends Exception
{
  /**
   * Construct an exception.
   *
   * @param message The exception message
   */

  public JaException(
    final String message)
  {
    super(Objects.requireNonNull(message, "Message"));
  }

  /**
   * Construct an exception.
   *
   * @param message The exception message
   * @param cause   The cause
   */

  public JaException(
    final String message,
    final Throwable cause)
  {
    super(
      Objects.requireNonNull(message, "Message"),
      Objects.requireNonNull(cause, "Cause"));
  }

  /**
   * Construct an exception.
   *
   * @param cause The cause
   */

  public JaException(
    final Throwable cause)
  {
    super(Objects.requireNonNull(cause, "Cause"));
  }
}
