package com.io7m.jaccord.tests.core.examples;

import com.io7m.jaccord.parser.api.JaAccidentalEncoding;
import com.io7m.jaccord.parser.api.JaChordNoteParserConfiguration;
import com.io7m.jaccord.parser.api.JaParserConfigurationType;
import com.io7m.jaccord.parser.vanilla.JaNoteParsing;
import com.io7m.jlexing.core.LexicalPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public final class JaScalesContainingDemo
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JaScalesContainingDemo.class);

  private JaScalesContainingDemo()
  {

  }

  public static void main(
    final String[] args)
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
        if (line_trimmed.isEmpty()) {
          continue;
        }

        final JaParserConfigurationType config =
          JaChordNoteParserConfiguration.builder()
            .setAccidentals(JaAccidentalEncoding.UNICODE_AND_ASCII_ACCIDENTALS)
            .build();

        final var position =
          LexicalPosition.<Path>of(0, 0, Optional.empty());

        final var notes =
          Arrays.stream(line_trimmed.split("\\s+"))
            .map(x -> JaNoteParsing.parseNote(config, position, x))
            .map(v -> {
              if (v.isValid()) {
                return v.get();
              }
              throw new IllegalArgumentException("Invalid note");
            })
            .collect(Collectors.toList());


      }
    }
  }
}
