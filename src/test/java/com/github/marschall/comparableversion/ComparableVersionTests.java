package com.github.marschall.comparableversion;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ComparableVersionTests {

  static Stream<Function<String, ?>> parameters() {
    return Stream.of(org.apache.maven.artifact.versioning.ComparableVersion::new, ComparableVersion::new);
  }

  @ParameterizedTest
  @MethodSource("parameters")
  <V extends Comparable<V>> void ignoreZeros(Function<String, V> parser) {
    V oneDotZero = parser.apply("1.0");
    V oneDotZeroDotZero = parser.apply("1.0.0");
    V oneDotZeroZero = parser.apply("1.00");

    assertThat(oneDotZero, comparesEqualTo(oneDotZeroDotZero));
    assertThat(oneDotZero, comparesEqualTo(oneDotZeroZero));

    assertEquals(oneDotZero, oneDotZeroDotZero);
    assertEquals(oneDotZero, oneDotZeroZero);

    assertEquals(oneDotZero.hashCode(), oneDotZeroDotZero.hashCode());
    assertEquals(oneDotZero.hashCode(), oneDotZeroZero.hashCode());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  <V extends Comparable<V>> void preserveZeros(Function<String, V> parser) {
    V oneDotZero = parser.apply("1.0");
    V oneDotZeroDotZero = parser.apply("1.0.0");
    V oneDotZeroZero = parser.apply("1.00");

    assertEquals("1.0", oneDotZero.toString());
    assertEquals("1.0.0", oneDotZeroDotZero.toString());
    assertEquals("1.00", oneDotZeroZero.toString());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  <V extends Comparable<V>> void compare(Function<String, V> parser) {
    List<String> unparsed = List.of("0.9", "0.9.1", "1.0", "1.0.1");

    List<V> versionsOrdered = unparsed.stream().map(parser::apply).collect(toList());

    for (int i = 0; i < versionsOrdered.size(); i++) {
      V version = versionsOrdered.get(i);

      for (int j = 0; j < i; j++) {
        V smallerVersion = versionsOrdered.get(j);
        assertThat(version, greaterThan(smallerVersion));
      } 

      assertThat(version, comparesEqualTo(version));

      for (int j = i + 1; j < versionsOrdered.size(); j++) {
        V greaterVersion = versionsOrdered.get(j);
        assertThat(version, lessThan(greaterVersion));
      } 
    }
  }

  @ParameterizedTest
  @MethodSource("parameters")
  <V extends Comparable<V>> void compareUnsigned(Function<String, V> parser) {
    List<String> unparsed = List.of("0", "1", "126", "127", "128", "255");

    List<V> versionsOrdered = unparsed.stream().map(parser::apply).collect(toList());

    for (int i = 0; i < versionsOrdered.size(); i++) {
      V version = versionsOrdered.get(i);

      for (int j = 0; j < i; j++) {
        V smallerVersion = versionsOrdered.get(j);
        assertThat(version, greaterThan(smallerVersion));
      } 

      assertThat(version, comparesEqualTo(version));

      for (int j = i + 1; j < versionsOrdered.size(); j++) {
        V greaterVersion = versionsOrdered.get(j);
        assertThat(version, lessThan(greaterVersion));
      } 
    }
  }

}
