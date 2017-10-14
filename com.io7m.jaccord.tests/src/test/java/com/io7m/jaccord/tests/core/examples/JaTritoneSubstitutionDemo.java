package com.io7m.jaccord.tests.core.examples;

import com.io7m.jaccord.chord_names.vanilla.JaChordNames;
import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordIntervals;
import com.io7m.jaccord.core.JaNote;
import io.vavr.collection.TreeSet;

public final class JaTritoneSubstitutionDemo
{
  private JaTritoneSubstitutionDemo()
  {

  }

  public static void main(
    final String[] args)
  {
    final JaChord base =
      JaChord.of(JaNote.A,
                 JaChordIntervals.of(
                   TreeSet.of(Integer.valueOf(4),
                              Integer.valueOf(7),
                              Integer.valueOf(10))));

    System.out.printf(
      "Base: %s%s\n",
      base.root(),
      JaChordNames.name(base.intervals()));

    System.out.printf(
      "Base Notes: %s\n",
      base.notes());

    final JaChord sub =
      JaChord.of(base.root().stepBy(-6), base.intervals());

    System.out.printf(
      "Sub: %s%s\n",
      sub.root(),
      JaChordNames.name(sub.intervals()));

    System.out.printf(
      "Sub Notes: %s\n",
      sub.notes());
  }
}
