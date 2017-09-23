package com.io7m.jaccord.tests.core;

import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.core.JaScale;
import com.io7m.jaccord.core.JaScaleIntervals;
import io.vavr.collection.TreeSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public final class JaSemitones
{
  private JaSemitones()
  {

  }

  public static void main(final String[] args)
    throws IOException
  {
    try (final BufferedReader reader =
           new BufferedReader(
             new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

      while (true) {
        final String line = reader.readLine();
        if (line == null) {
          break;
        }
        final String line_trimmed = line.trim();

        TreeSet<Integer> intervals = TreeSet.empty();
        final String[] split = line_trimmed.split(" ");
        for (final String segment : split) {
          switch (segment) {
            case "1":
              break;
            case "♯1":
              intervals = intervals.add(Integer.valueOf(1));
              break;

            case "♭2":
              intervals = intervals.add(Integer.valueOf(1));
              break;
            case "2":
              intervals = intervals.add(Integer.valueOf(2));
              break;
            case "♯2":
              intervals = intervals.add(Integer.valueOf(3));
              break;

            case "♭3":
              intervals = intervals.add(Integer.valueOf(3));
              break;
            case "3":
              intervals = intervals.add(Integer.valueOf(4));
              break;
            case "♯3":
              intervals = intervals.add(Integer.valueOf(5));
              break;

            case "4":
              intervals = intervals.add(Integer.valueOf(5));
              break;
            case "♯4":
              intervals = intervals.add(Integer.valueOf(6));
              break;
            case "♭4":
              intervals = intervals.add(Integer.valueOf(4));
              break;

            case "5":
              intervals = intervals.add(Integer.valueOf(7));
              break;
            case "♯5":
              intervals = intervals.add(Integer.valueOf(8));
              break;
            case "♭5":
              intervals = intervals.add(Integer.valueOf(6));
              break;

            case "6":
              intervals = intervals.add(Integer.valueOf(9));
              break;
            case "♯6":
              intervals = intervals.add(Integer.valueOf(10));
              break;
            case "♭6":
              intervals = intervals.add(Integer.valueOf(8));
              break;

            case "7":
              intervals = intervals.add(Integer.valueOf(11));
              break;
            case "♭7":
              intervals = intervals.add(Integer.valueOf(10));
              break;

            default: {
              throw new IllegalArgumentException("Bad input: " + segment);
            }
          }
        }

        final JaScale scale =
          JaScale.of(JaNote.C, JaScaleIntervals.of(intervals));

        System.out.println(
          scale.intervals()
            .intervals()
            .toVector()
            .sorted()
            .map(Object::toString)
            .collect(Collectors.joining(" ")));
      }
    }
  }
}
