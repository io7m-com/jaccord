package com.io7m.jaccord.tests.core.examples;

import com.io7m.jaccord.core.JaNote;

import java.util.Comparator;
import java.util.TreeSet;

public final class JaQuartal
{
  private JaQuartal()
  {

  }

  public static void main(
    final String[] args)
  {
    JaNote note = JaNote.C;

    final TreeSet<JaNote> notes =
      new TreeSet<>(Comparator.comparing(JaNote::noteName));

    boolean first = true;
    while (true) {
      if (note == JaNote.C && !first) {
        break;
      }
      System.out.println("note: " + note);
      first = false;
      notes.add(note);
      note = note.stepBy(5);
    }

    System.out.println("notes: " + notes);
  }
}
