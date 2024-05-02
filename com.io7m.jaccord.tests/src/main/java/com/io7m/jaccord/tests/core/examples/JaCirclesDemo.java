package com.io7m.jaccord.tests.core.examples;

import com.io7m.jaccord.core.JaNote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JaCirclesDemo
{
  private static final Logger LOG = LoggerFactory.getLogger(JaCirclesDemo.class);

  private JaCirclesDemo()
  {

  }

  public static void main(
    final String[] args)
  {
    final JaNote root = JaNote.C;
    JaNote next = root.stepBy(-7);

    LOG.debug("note: {}", root);

    while (true) {
      if (root == next) {
        break;
      }

      LOG.debug("note: {} ({})", next, Integer.valueOf(root.intervalUpTo(next)));
      next = next.stepBy(-7);
    }
  }
}
