package com.io7m.jaccord.tests.core.examples;

import com.io7m.jaccord.core.JaScaleNamed;
import com.io7m.jaccord.scales.api.JaScales;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class JaScaleSums
{
  private static final Logger LOG = LoggerFactory.getLogger(JaScaleSums.class);

  private JaScaleSums()
  {

  }

  public static void main(
    final String[] args)
  {
    final List<Tuple2<JaScaleNamed, Number>> sorted = new ArrayList<>(128);

    for (final String scale_name : JaScales.scales()) {
      for (final JaScaleNamed scale : JaScales.scalesByID(scale_name)) {
        final Number sum = scale.intervals().intervals().sum();
        sorted.add(Tuple.of(scale, sum));
      }
    }

    sorted.sort(Comparator.comparingInt(o -> o._2.intValue()));

    sorted.forEach(pair -> {
      final int interval_ci = pair._1.intervals().intervals().size();
      final double interval_count = (double) interval_ci;
      final int interval_sum_i = pair._2.intValue();
      final double interval_sum = (double) interval_sum_i;
      final double interval_coeff = interval_count / interval_sum;

      System.out.printf("%-32s %d\t%d\t%.2f\n",
                        pair._1.name(),
                        Integer.valueOf(interval_ci),
                        Integer.valueOf(interval_sum_i),
                        Double.valueOf(interval_coeff));
    });
  }
}
