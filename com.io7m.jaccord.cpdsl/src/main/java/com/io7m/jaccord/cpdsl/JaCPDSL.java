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

package com.io7m.jaccord.cpdsl;

import com.io7m.jaccord.chord_names.api.JaChordNamesType;
import com.io7m.jaccord.core.JaChord;
import com.io7m.jaccord.core.JaChordIntervals;
import com.io7m.jaccord.core.JaChordInversions;
import com.io7m.jaccord.core.JaIntervals;
import com.io7m.jaccord.core.JaNote;
import com.io7m.jaccord.core.JaScale;
import com.io7m.jaccord.core.JaScaleHarmonization;
import com.io7m.jaccord.core.JaScaleHarmonizationChordTypes;
import com.io7m.jaccord.core.JaScaleNamed;
import com.io7m.jaccord.scales.api.JaScales;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import io.vavr.collection.Vector;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * A domain-specific language for describing chord progressions.
 */

public final class JaCPDSL
{
  private JaChordNamesType names;

  private JaCPDSL(
    final JaChordNamesType in_names)
  {
    this.names = NullCheck.notNull(in_names, "Names");
  }

  /**
   * @return A new DSL
   */

  public static JaCPDSL create()
  {
    final ServiceLoader<JaChordNamesType> names_loader =
      ServiceLoader.load(JaChordNamesType.class);
    final Iterator<JaChordNamesType> names_iter =
      names_loader.iterator();

    while (names_iter.hasNext()) {
      final JaChordNamesType names = names_iter.next();
      return new JaCPDSL(names);
    }

    throw new UnimplementedCodeException();
  }

  /**
   * Look up a scale with the given root node and identifier.
   *
   * @param root The root note
   * @param name The identifier
   *
   * @return A scale, if any
   *
   * @see JaScales
   */

  public Scale scale(
    final JaNote root,
    final String name)
  {
    NullCheck.notNull(root, "Root");
    NullCheck.notNull(name, "Name");

    final List<JaScaleNamed> matching = JaScales.scalesByID(name);

    if (matching.isEmpty()) {
      throw new UnimplementedCodeException();
    }

    if (matching.size() == 1) {
      return new Scale(root, matching.get(0));
    }

    throw new UnimplementedCodeException();
  }

  /**
   * Transpose a chord chromatically.
   *
   * @param chord The chord
   * @param steps The steps upwards (or downwards if negative)
   *
   * @return A transposed chord
   */

  public ChordTermType chromaticTranspose(
    final ChordTermType chord,
    final int steps)
  {
    return new ChordChromaticTranspose(chord, steps);
  }

  /**
   * Substitute a chord with a chromatic mediant.
   *
   * @param mediant The type of mediant
   * @param chord   The chord
   *
   * @return A substituted chord
   */

  public ChordTermType chromaticMediant(
    final ChordTermType chord,
    final ChromaticMediant mediant)
  {
    return new ChordChromaticMediant(mediant, chord);
  }

  /**
   * A chord change.
   *
   * @param chord The chord
   * @param beats The number of beats that the chord is held
   *
   * @return The chord change
   */

  public Change change(
    final ChordTermType chord,
    final int beats)
  {
    NullCheck.notNull(chord, "Chord");
    return new Change(chord, beats);
  }

  /**
   * Build a chord progression.
   *
   * @param changes The list of chord changes
   *
   * @return A chord progression
   */

  public Progression progression(
    final Change... changes)
  {
    return new Progression(Vector.of(
      NullCheck.notNull(changes, "Changes")));
  }

  /**
   * Determine the triad at the given degree of the given scale.
   *
   * @param scale  The scale
   * @param degree The degree
   *
   * @return The diatonic chord
   */

  public ChordDiatonic diatonic(
    final Scale scale,
    final Degree degree)
  {
    NullCheck.notNull(scale, "Scale");
    NullCheck.notNull(degree, "Degree");

    final Vector<JaChord> triads =
      JaScaleHarmonization.harmonize(
        JaScaleHarmonizationChordTypes.TRIADS, scale.scale);

    if (degree.ordinal() < triads.size()) {
      return new ChordDiatonic(scale, degree, triads.get(degree.ordinal()));
    }

    throw new UnimplementedCodeException();
  }

  /**
   * Determine the suspended 4th chord at the given degree of the given scale.
   *
   * @param scale  The scale
   * @param degree The degree
   *
   * @return The diatonic chord
   */

  public ChordDiatonic sus4(
    final Scale scale,
    final Degree degree)
  {
    NullCheck.notNull(scale, "Scale");
    NullCheck.notNull(degree, "Degree");

    final Vector<JaChord> triads =
      JaScaleHarmonization.harmonize(
        JaScaleHarmonizationChordTypes.SUSPENDED_4_CHORDS, scale.scale);

    if (degree.ordinal() < triads.size()) {
      return new ChordDiatonic(scale, degree, triads.get(degree.ordinal()));
    }

    throw new UnimplementedCodeException();
  }

  /**
   * Determine the suspended 2nd chord at the given degree of the given scale.
   *
   * @param scale  The scale
   * @param degree The degree
   *
   * @return The diatonic chord
   */

  public ChordDiatonic sus2(
    final Scale scale,
    final Degree degree)
  {
    NullCheck.notNull(scale, "Scale");
    NullCheck.notNull(degree, "Degree");

    final Vector<JaChord> triads =
      JaScaleHarmonization.harmonize(
        JaScaleHarmonizationChordTypes.SUSPENDED_2_CHORDS, scale.scale);

    if (degree.ordinal() < triads.size()) {
      return new ChordDiatonic(scale, degree, triads.get(degree.ordinal()));
    }

    throw new UnimplementedCodeException();
  }

  /**
   * Determine the inversion of the given chord.
   *
   * @param chord The chord
   *
   * @return The inversion of the chord
   */

  public ChordInversion inversion(
    final ChordTermType chord)
  {
    NullCheck.notNull(chord, "Chord");
    return new ChordInversion(chord);
  }

  /**
   * Determine the secondary dominant of the given chord.
   *
   * @param chord The chord
   *
   * @return The secondary dominant of the chord
   */

  public ChordSecondaryDominant secondaryDominant(
    final ChordTermType chord)
  {
    NullCheck.notNull(chord, "Chord");
    return new ChordSecondaryDominant(chord);
  }

  /**
   * Determine the tritone substitution of the given chord.
   *
   * @param chord The chord
   *
   * @return The tritone substitution of the chord
   */

  public ChordTritone tritone(
    final ChordTermType chord)
  {
    NullCheck.notNull(chord, "Chord");
    return new ChordTritone(chord);
  }

  /**
   * Determine the tritone substitution of the secondary dominant of the given
   * chord.
   *
   * @param chord The chord
   *
   * @return The tritone substitution of the secondary dominant of the chord
   */

  public ChordTritone tritoneSecondaryDominant(
    final ChordTermType chord)
  {
    NullCheck.notNull(chord, "Chord");
    return this.tritone(this.secondaryDominant(chord));
  }

  /**
   * Determine the seventh chord at the given degree of the given scale.
   *
   * @param scale  The scale
   * @param degree The degree
   *
   * @return The diatonic chord
   */

  public ChordDiatonic diatonic7(
    final Scale scale,
    final Degree degree)
  {
    NullCheck.notNull(scale, "Scale");
    NullCheck.notNull(degree, "Degree");

    final Vector<JaChord> triads =
      JaScaleHarmonization.harmonize(
        JaScaleHarmonizationChordTypes.SEVENTH_CHORDS, scale.scale);

    if (degree.ordinal() < triads.size()) {
      return new ChordDiatonic(scale, degree, triads.get(degree.ordinal()));
    }

    throw new UnimplementedCodeException();
  }

  /**
   * Determine the ninth chord at the given degree of the given scale.
   *
   * @param scale  The scale
   * @param degree The degree
   *
   * @return The diatonic chord
   */

  public ChordDiatonic diatonic9(
    final Scale scale,
    final Degree degree)
  {
    NullCheck.notNull(scale, "Scale");
    NullCheck.notNull(degree, "Degree");

    final Vector<JaChord> triads =
      JaScaleHarmonization.harmonize(
        JaScaleHarmonizationChordTypes.NINTH_CHORDS, scale.scale);

    if (degree.ordinal() < triads.size()) {
      return new ChordDiatonic(scale, degree, triads.get(degree.ordinal()));
    }

    throw new UnimplementedCodeException();
  }

  /**
   * Determine the eleventh chord at the given degree of the given scale.
   *
   * @param scale  The scale
   * @param degree The degree
   *
   * @return The diatonic chord
   */

  public ChordDiatonic diatonic11(
    final Scale scale,
    final Degree degree)
  {
    NullCheck.notNull(scale, "Scale");
    NullCheck.notNull(degree, "Degree");

    final Vector<JaChord> triads =
      JaScaleHarmonization.harmonize(
        JaScaleHarmonizationChordTypes.ELEVENTH_CHORDS, scale.scale);

    if (degree.ordinal() < triads.size()) {
      return new ChordDiatonic(scale, degree, triads.get(degree.ordinal()));
    }

    throw new UnimplementedCodeException();
  }

  /**
   * Alter a given chord.
   *
   * @param input   The input chord
   * @param add     The additions to the chord
   * @param replace The replacements of the chord tones
   *
   * @return An altered chord
   */

  public ChordAltered alteredAddedReplaced(
    final ChordTermType input,
    final Set<Integer> add,
    final Map<Integer, Integer> replace)
  {
    return new ChordAltered(input, add, replace);
  }

  /**
   * Alter a given chord.
   *
   * @param input   The input chord
   * @param replace The replacements of the chord tones
   *
   * @return An altered chord
   */

  public ChordAltered alteredReplaced(
    final ChordTermType input,
    final Map<Integer, Integer> replace)
  {
    return new ChordAltered(input, TreeSet.empty(), replace);
  }

  /**
   * Alter a given chord.
   *
   * @param input The input chord
   * @param add   The additions to the chord
   *
   * @return An altered chord
   */

  public ChordAltered alteredAdded(
    final ChordTermType input,
    final Set<Integer> add)
  {
    return new ChordAltered(input, add, HashMap.empty());
  }

  /**
   * A scale degree.
   */

  public enum Degree
  {
    /**
     * The first scale degree.
     */

    I,

    /**
     * The second scale degree.
     */

    II,

    /**
     * The third scale degree.
     */

    III,

    /**
     * The fourth scale degree.
     */

    IV,

    /**
     * The fifth scale degree.
     */

    V,

    /**
     * The sixth scale degree.
     */

    VI,

    /**
     * The seventh scale degree.
     */

    VII
  }

  /**
   * A chord term.
   */

  public interface ChordTermType
  {
    /**
     * Evaluate and return the chord
     *
     * @return The evaluated chord
     */

    JaChord evaluate();

    /**
     * @return The type of chord term
     */

    Type type();

    /**
     * A value that specifies the type of chord term.
     */

    enum Type
    {
      /**
       * A diatonic chord.
       */

      CHORD_DIATONIC,

      /**
       * A chromatically transposed chord.
       */

      CHORD_CHROMATIC_TRANSPOSE,

      /**
       * A secondary dominant chord.
       */

      CHORD_SECONDARY_DOMINANT,

      /**
       * The first inversion of a chord.
       */

      CHORD_INVERSION,

      /**
       * A chord substituted with a chord a tritone away.
       */

      CHORD_TRITONE,

      /**
       * A chord altered with the replacement or addition of specific notes.
       */

      CHORD_ALTERED,

      /**
       * A chromatic mediant substitution of a given chord.
       */

      CHORD_CHROMATIC_MEDIANT
    }
  }

  /**
   * A scale.
   */

  public final class Scale
  {
    private final JaNote root;
    private final JaScaleNamed scale_named;
    private final JaScale scale;

    private Scale(
      final JaNote in_root,
      final JaScaleNamed in_scale)
    {
      this.root = NullCheck.notNull(in_root, "Root");
      this.scale_named = NullCheck.notNull(in_scale, "Scale");
      this.scale = JaScale.of(this.root, this.scale_named.intervals());
    }

    /**
     * @return The named scale
     */

    public JaScaleNamed scaleNamed()
    {
      return this.scale_named;
    }

    /**
     * @return The scale
     */

    public JaScale scale()
    {
      return this.scale;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.root.noteName());
      sb.append(" ");
      sb.append(this.scale_named.name());
      return sb.toString();
    }
  }

  /**
   * A chord progression.
   */

  public final class Progression
  {
    private final Vector<Change> changes;

    private Progression(
      final Vector<Change> in_changes)
    {
      this.changes = NullCheck.notNull(in_changes, "Changes");
    }

    /**
     * @return The list of chord changes
     */

    public Vector<Change> changes()
    {
      return this.changes;
    }

    @Override
    public String toString()
    {
      return this.changes.toStream()
        .map(Change::toString)
        .collect(Collectors.joining(" "));
    }
  }

  /**
   * A chord change.
   */

  public final class Change
  {
    private final ChordTermType chord;
    private final int beats;

    private Change(
      final ChordTermType in_chord,
      final int in_beats)
    {
      this.chord = NullCheck.notNull(in_chord, "Chord");
      this.beats = in_beats;
    }

    /**
     * @return The chord term associated with the change
     */

    public ChordTermType chord()
    {
      return this.chord;
    }

    /**
     * @return The number of beats that the chord is held
     */

    public int beats()
    {
      return this.beats;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.chord.toString());
      sb.append(" (");
      sb.append(this.beats);
      sb.append(")");
      return sb.toString();
    }
  }

  /**
   * A diatonic chord.
   */

  public final class ChordDiatonic implements ChordTermType
  {
    private final Scale scale;
    private final Degree degree;
    private final JaChord chord;

    private ChordDiatonic(
      final Scale in_scale,
      final Degree in_degree,
      final JaChord in_chord)
    {
      this.scale = NullCheck.notNull(in_scale, "Scale");
      this.degree = NullCheck.notNull(in_degree, "Degree");
      this.chord = NullCheck.notNull(in_chord, "Chord");
    }

    @Override
    public JaChord evaluate()
    {
      return this.chord;
    }

    @Override
    public Type type()
    {
      return Type.CHORD_DIATONIC;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.chord.root().noteName());
      sb.append(JaCPDSL.this.names.name(this.chord.intervals()));
      return sb.toString();
    }
  }

  /**
   * An inverted chord.
   */

  public final class ChordInversion implements ChordTermType
  {
    private final ChordTermType input;
    private final JaChord output;

    private ChordInversion(
      final ChordTermType in_input)
    {
      this.input = NullCheck.notNull(in_input, "Chord");
      this.output = this.evaluateEager();
    }

    @Override
    public JaChord evaluate()
    {
      return this.output;
    }

    private JaChord evaluateEager()
    {
      final JaChord e = this.input.evaluate();
      return JaChordInversions.invert(e);
    }

    @Override
    public Type type()
    {
      return Type.CHORD_INVERSION;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.output.root().noteName());
      sb.append(JaCPDSL.this.names.name(this.output.intervals()));
      return sb.toString();
    }
  }

  /**
   * The type of chromatic mediant.
   */

  public enum ChromaticMediant
  {
    /**
     * The mediant a major third up.
     */

    MAJOR_UP,

    /**
     * The mediant a major third down.
     */

    MAJOR_DOWN,

    /**
     * The mediant a minor third up.
     */

    MINOR_UP,

    /**
     * The mediant a minor third down.
     */

    MINOR_DOWN
  }

  /**
   * A chromatic-mediant subtituted chord.
   */

  public final class ChordChromaticMediant implements ChordTermType
  {
    private final ChordTermType input;
    private final JaChord output;
    private final ChromaticMediant mediant;

    private ChordChromaticMediant(
      final ChromaticMediant in_mediant,
      final ChordTermType in_input)
    {
      this.mediant = NullCheck.notNull(in_mediant, "Mediant");
      this.input = NullCheck.notNull(in_input, "Chord");
      this.output = this.evaluateEager();
    }

    @Override
    public JaChord evaluate()
    {
      return this.output;
    }

    private JaChord evaluateEager()
    {
      final JaChord e = this.input.evaluate();

      switch (this.mediant) {
        case MAJOR_UP:
          return JaChord.of(e.root().stepBy(4), e.intervals());
        case MAJOR_DOWN:
          return JaChord.of(e.root().stepBy(-4), e.intervals());
        case MINOR_UP:
          return JaChord.of(e.root().stepBy(3), e.intervals());
        case MINOR_DOWN:
          return JaChord.of(e.root().stepBy(-3), e.intervals());
      }

      throw new UnreachableCodeException();
    }

    @Override
    public Type type()
    {
      return Type.CHORD_CHROMATIC_MEDIANT;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.output.root().noteName());
      sb.append(JaCPDSL.this.names.name(this.output.intervals()));
      return sb.toString();
    }
  }

  /**
   * A tritone-subtituted chord.
   */

  public final class ChordTritone implements ChordTermType
  {
    private final ChordTermType input;
    private final JaChord output;

    private ChordTritone(
      final ChordTermType in_input)
    {
      this.input = NullCheck.notNull(in_input, "Chord");
      this.output = this.evaluateEager();
    }

    @Override
    public JaChord evaluate()
    {
      return this.output;
    }

    private JaChord evaluateEager()
    {
      final JaChord e = this.input.evaluate();
      return JaChord.of(e.root().stepBy(6), e.intervals());
    }

    @Override
    public Type type()
    {
      return Type.CHORD_TRITONE;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.output.root().noteName());
      sb.append(JaCPDSL.this.names.name(this.output.intervals()));
      return sb.toString();
    }
  }

  /**
   * An altered chord.
   */

  public final class ChordAltered implements ChordTermType
  {
    private final ChordTermType input;
    private final JaChord output;
    private final Set<Integer> add;
    private final Map<Integer, Integer> replace;

    private ChordAltered(
      final ChordTermType in_input,
      final Set<Integer> in_add,
      final Map<Integer, Integer> in_replace)
    {
      this.input = NullCheck.notNull(in_input, "Chord");
      this.add = NullCheck.notNull(in_add, "Add");
      this.replace = NullCheck.notNull(in_replace, "Replace");
      this.output = this.evaluateEager();
    }

    @Override
    public JaChord evaluate()
    {
      return this.output;
    }

    private JaChord evaluateEager()
    {
      final JaChord e = this.input.evaluate();
      final SortedSet<Integer> r =
        e.intervals()
          .intervalsNormalized()
          .map(i -> this.replace.getOrElse(i, i));
      final SortedSet<Integer> a = r.addAll(this.add);
      return JaChord.of(e.root(), JaChordIntervals.of(a));
    }

    @Override
    public Type type()
    {
      return Type.CHORD_ALTERED;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.output.root().noteName());
      sb.append(JaCPDSL.this.names.name(this.output.intervals()));
      return sb.toString();
    }
  }

  /**
   * A chromatically transposed chord.
   */

  public final class ChordChromaticTranspose implements ChordTermType
  {
    private final ChordTermType input;
    private final int steps;
    private final JaChord output;

    private ChordChromaticTranspose(
      final ChordTermType in_input,
      final int in_steps)
    {
      this.input = NullCheck.notNull(in_input, "Chord");
      this.steps = in_steps;
      this.output = this.evaluateEager();
    }

    @Override
    public JaChord evaluate()
    {
      return this.output;
    }

    private JaChord evaluateEager()
    {
      final JaChord e = this.input.evaluate();
      return JaChord.of(e.root().stepBy(this.steps), e.intervals());
    }

    @Override
    public Type type()
    {
      return Type.CHORD_CHROMATIC_TRANSPOSE;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.output.root().noteName());
      sb.append(JaCPDSL.this.names.name(this.output.intervals()));
      return sb.toString();
    }
  }

  /**
   * A secondary dominant chord.
   */

  public final class ChordSecondaryDominant implements ChordTermType
  {
    private final ChordTermType input;
    private final JaChord output;

    private ChordSecondaryDominant(
      final ChordTermType in_input)
    {
      this.input = NullCheck.notNull(in_input, "Chord");
      this.output = this.evaluateEager();
    }

    @Override
    public JaChord evaluate()
    {
      return this.output;
    }

    private JaChord evaluateEager()
    {
      final JaChord e = this.input.evaluate();
      return JaChord.of(
        e.root().stepBy(JaIntervals.FIFTH.intValue()),
        JaChordIntervals.of(TreeSet.of(
          JaIntervals.MAJOR_THIRD,
          JaIntervals.FIFTH,
          JaIntervals.MINOR_SEVENTH)));
    }

    @Override
    public Type type()
    {
      return Type.CHORD_SECONDARY_DOMINANT;
    }

    @Override
    public String toString()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.output.root().noteName());
      sb.append(JaCPDSL.this.names.name(this.output.intervals()));
      return sb.toString();
    }
  }
}
