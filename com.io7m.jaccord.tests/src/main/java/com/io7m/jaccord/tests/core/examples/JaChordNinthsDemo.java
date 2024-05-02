package com.io7m.jaccord.tests.core.examples;

import com.io7m.jaccord.chord_names.vanilla.JaChordNames;
import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordInversions;
import com.io7m.jaccord.core.JaChordSlash;
import com.io7m.jaccord.core.JaChordSlashes;
import com.io7m.jaccord.core.JaErrorType;
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.parser.api.JaAccidentalEncoding;
import com.io7m.jaccord.parser.api.JaChordNoteParserConfiguration;
import com.io7m.jaccord.parser.api.JaChordNoteParserType;
import com.io7m.jaccord.parser.api.JaParseError;
import com.io7m.jaccord.parser.vanilla.JaChordNoteParsers;
import com.io7m.jlexing.core.LexicalPosition;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import io.vavr.control.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public final class JaChordNinthsDemo
{
  private static final Logger LOG = LoggerFactory.getLogger(JaChordNinthsDemo.class);

  private JaChordNinthsDemo()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    try (final BufferedReader reader =
           new BufferedReader(
             new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

      final JaChordNoteParserConfiguration config =
        JaChordNoteParserConfiguration.builder()
          .setAccidentals(JaAccidentalEncoding.UNICODE_AND_ASCII_ACCIDENTALS)
          .build();

      final JaChordNoteParserType parser =
        new JaChordNoteParsers().create(Paths.get(""), config);

      while (true) {
        final String line = reader.readLine();
        if (line == null) {
          break;
        }
        final String line_trimmed = line.trim();
        if (line_trimmed.isEmpty()) {
          continue;
        }

        final Validation<Seq<JaParseError>, JaChord> r =
          parser.parseLine(line_trimmed);

        if (r.isInvalid()) {
          r.getError().forEach(JaChordNinthsDemo::logParseError);
          continue;
        }

        final JaChord base = r.get();
        forChord(base);
      }
    }
  }

  public static void forChord(
    final JaChord base)
  {
    final String name = JaChordNames.name(base.intervals());

    System.out.println(
      String.format(
        "%-32s : %s%s (%s)",
        "Base",
        base.root().noteName(),
        name,
        base.notes().map(JaNote::noteName).collect(Collectors.joining(" "))));

    try {
      final Optional<JaChordSlash> base_slash_opt =
        JaChordSlashes.slash(base);

      base_slash_opt.ifPresent(base_slash -> {
        final String slash_name =
          JaChordNames.name(base_slash.chord().intervals());

        System.out.println(
          String.format(
            "%-32s : %s%s/%s",
            "Slash",
            base_slash.chord().root().noteName(),
            slash_name,
            base_slash.bass().noteName()));
      });

      final Vector<JaChord> inversions =
        JaChordInversions.inversions(base);
      for (int index = 0; index < inversions.size(); ++index) {
        final JaChord inversion = inversions.get(index);
        final String r_inversion_name =
          JaChordNames.name(inversion.intervals());

        System.out.println(
          String.format(
            "%-32s : %s%s (%s)",
            String.format("Inversion %d", Integer.valueOf(index)),
            inversion.root().noteName(),
            r_inversion_name,
            inversion.notes().map(JaNote::noteName).collect(Collectors.joining(
              " "))));

        final Optional<JaChordSlash> inversion_slash_opt =
          JaChordSlashes.slash(inversion);

        final int final_index = index;
        inversion_slash_opt.ifPresent(inversion_slash -> {

          final String inversion_slash_name =
            JaChordNames.name(inversion_slash.chord().intervals());

          System.out.println(
            String.format(
              "%-32s : %s%s/%s",
              String.format(
                "Slash Inversion %d ",
                Integer.valueOf(final_index)),
              inversion_slash.chord().root().noteName(),
              inversion_slash_name,
              inversion_slash.bass().noteName()));
        });

      }
    } catch (final Exception e) {
      LOG.error("", e);
    }
  }

  private static void logError(
    final JaErrorType error)
  {
    LOG.error("{}", error.message());
    error.exception().ifPresent(ex -> LOG.error("", ex));
  }

  private static void logParseError(
    final JaParseError error)
  {
    final LexicalPosition<Path> position = error.position();
    if (position.file().isPresent()) {
      final Path file = position.file().get();
      LOG.error(
        "{}:{}: {}",
        file,
        Integer.valueOf(position.line()),
        error.message());
    } else {
      LOG.error(
        "{}: {}",
        Integer.valueOf(position.line()),
        error.message());
    }
    error.exception().ifPresent(ex -> LOG.error("", ex));
  }
}
